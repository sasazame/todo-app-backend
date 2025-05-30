# データベース設計書

## 概要
PostgreSQL 16を使用したTODOアプリケーションのデータベース設計

## ERダイアグラム
```
┌─────────────────────────────────────────────┐
│                   users                     │
├─────────────────────────────────────────────┤
│ id (BIGSERIAL) PK                          │
│ email (VARCHAR(255)) NOT NULL UNIQUE       │
│ password (VARCHAR(255)) NOT NULL           │
│ first_name (VARCHAR(50)) NOT NULL          │
│ last_name (VARCHAR(50)) NOT NULL           │
│ enabled (BOOLEAN) NOT NULL DEFAULT TRUE    │
│ created_at (TIMESTAMP) NOT NULL            │
│ updated_at (TIMESTAMP) NOT NULL            │
└─────────────────────────────────────────────┘
                          │
                          │ 1:N
                          ▼
┌─────────────────────────────────────────────┐
│                   todos                     │
├─────────────────────────────────────────────┤
│ id (BIGSERIAL) PK                          │
│ user_id (BIGINT) NOT NULL FK               │
│ title (VARCHAR(255)) NOT NULL              │
│ description (TEXT)                         │
│ status (VARCHAR(20)) NOT NULL              │
│ priority (VARCHAR(10)) NOT NULL            │
│ due_date (DATE)                            │
│ created_at (TIMESTAMPTZ) NOT NULL          │
│ updated_at (TIMESTAMPTZ) NOT NULL          │
└─────────────────────────────────────────────┘
```

## テーブル定義

### users テーブル
| カラム名 | データ型 | 制約 | 説明 |
|---------|----------|------|------|
| id | BIGSERIAL | PRIMARY KEY | 自動採番ID |
| email | VARCHAR(255) | NOT NULL, UNIQUE | メールアドレス |
| password | VARCHAR(255) | NOT NULL | ハッシュ化パスワード |
| first_name | VARCHAR(50) | NOT NULL | 名前 |
| last_name | VARCHAR(50) | NOT NULL | 姓 |
| enabled | BOOLEAN | NOT NULL, DEFAULT TRUE | アカウント有効状態 |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | 作成日時 |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | 更新日時 |

### todos テーブル
| カラム名 | データ型 | 制約 | 説明 |
|---------|----------|------|------|
| id | BIGSERIAL | PRIMARY KEY | 自動採番ID |
| user_id | BIGINT | NOT NULL, FK → users.id | 所有者ユーザーID |
| title | VARCHAR(255) | NOT NULL | TODOタイトル |
| description | TEXT | NULL | 詳細説明 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'TODO' | ステータス |
| priority | VARCHAR(10) | NOT NULL, DEFAULT 'MEDIUM' | 優先度 |
| due_date | DATE | NULL | 期限日 |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() | 作成日時 |
| updated_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() | 更新日時 |

## 制約

### 外部キー制約
```sql
-- TODO → ユーザー関連付け
ALTER TABLE todos ADD CONSTRAINT fk_todos_user_id 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

### CHECK制約
```sql
-- ステータス制約
ALTER TABLE todos ADD CONSTRAINT chk_status
    CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE'));

-- 優先度制約  
ALTER TABLE todos ADD CONSTRAINT chk_priority
    CHECK (priority IN ('HIGH', 'MEDIUM', 'LOW'));
```

### インデックス
```sql
-- users テーブル
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_enabled ON users(enabled);

-- todos テーブル
CREATE INDEX idx_todos_user_id ON todos(user_id);
CREATE INDEX idx_todos_user_status ON todos(user_id, status);
CREATE INDEX idx_todos_status ON todos(status);
CREATE INDEX idx_todos_due_date ON todos(due_date);
```

## トリガー

### 更新日時自動更新
```sql
-- 更新日時を自動更新する関数
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- トリガーの作成
CREATE TRIGGER update_todos_updated_at BEFORE UPDATE
    ON todos FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

## マイグレーション

### Flyway設定
- **ファイル配置**: `src/main/resources/db/migration/`
- **命名規則**: `V{version}__{description}.sql`
- **マイグレーション履歴**:
  - `V1__create_todo_table.sql`: TODOテーブル作成
  - `V2__create_user_table.sql`: ユーザーテーブル作成
  - `V3__add_user_id_to_todos.sql`: TODO-ユーザー関連付け

### 設定
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    baseline-version: 0
```

## セットアップ手順

### 1. データベース作成
```bash
sudo -u postgres psql -c "CREATE DATABASE todoapp;"
sudo -u postgres psql -c "CREATE USER todoapp WITH ENCRYPTED PASSWORD 'todoapp';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE todoapp TO todoapp;"
sudo -u postgres psql -c "ALTER DATABASE todoapp OWNER TO todoapp;"
```

### 2. 接続設定
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/todoapp
    username: todoapp
    password: todoapp
    driver-class-name: org.postgresql.Driver
```

## パフォーマンス考慮事項

### 接続プール（HikariCP）
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### クエリ最適化
1. **ユーザー別TODO検索**: `idx_todos_user_id` インデックス使用
2. **ユーザー・ステータス組み合わせ**: `idx_todos_user_status` 複合インデックス使用
3. **メール検索**: `idx_users_email` インデックス使用
4. **期限日検索**: `idx_todos_due_date` インデックス使用

### セキュリティ考慮事項
1. **パスワードハッシュ化**: BCrypt使用（コスト12）
2. **カスケード削除**: ユーザー削除時のTODO自動削除
3. **データ分離**: ユーザー別のTODOアクセス制御

## 現在の実装状況

### 実装済み機能
1. ✅ **users**: ユーザー管理（認証用）
2. ✅ **todos**: TODO管理（所有者制御付き）
3. ✅ **外部キー制約**: データ整合性保証
4. ✅ **インデックス**: パフォーマンス最適化

### 将来の拡張予定
1. **categories**: カテゴリー分類
2. **todo_categories**: TODO-カテゴリー中間テーブル
3. **attachments**: 添付ファイル
4. **notifications**: 通知管理

### 拡張時の設計方針
- **外部キー制約**: 参照整合性保証
- **論理削除**: deleted_at カラム追加
- **監査ログ**: created_by, updated_by カラム追加

## バックアップ・復旧
```bash
# バックアップ
pg_dump -h localhost -U todoapp -d todoapp > backup.sql

# 復旧
psql -h localhost -U todoapp -d todoapp < backup.sql
```