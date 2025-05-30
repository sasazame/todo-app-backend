# データベース設計書

## 概要
PostgreSQL 16を使用したTODOアプリケーションのデータベース設計

## ERダイアグラム
```
┌─────────────────────────────────────────────┐
│                   todos                     │
├─────────────────────────────────────────────┤
│ id (BIGSERIAL) PK                          │
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

### todos テーブル
| カラム名 | データ型 | 制約 | 説明 |
|---------|----------|------|------|
| id | BIGSERIAL | PRIMARY KEY | 自動採番ID |
| title | VARCHAR(255) | NOT NULL | TODOタイトル |
| description | TEXT | NULL | 詳細説明 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'TODO' | ステータス |
| priority | VARCHAR(10) | NOT NULL, DEFAULT 'MEDIUM' | 優先度 |
| due_date | DATE | NULL | 期限日 |
| created_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() | 作成日時 |
| updated_at | TIMESTAMPTZ | NOT NULL, DEFAULT NOW() | 更新日時 |

## 制約

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
-- パフォーマンス最適化のためのインデックス
CREATE INDEX idx_todos_status ON todos(status);
CREATE INDEX idx_todos_priority ON todos(priority);
CREATE INDEX idx_todos_due_date ON todos(due_date);
CREATE INDEX idx_todos_created_at ON todos(created_at DESC);
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
- **初期スキーマ**: `V1__create_todo_table.sql`

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
1. **ステータス検索**: `idx_todos_status` インデックス使用
2. **期限日検索**: `idx_todos_due_date` インデックス使用
3. **作成日順ソート**: `idx_todos_created_at` インデックス使用（降順）

## 将来の拡張

### 予定されるテーブル追加
1. **users**: ユーザー管理
2. **categories**: カテゴリー分類
3. **todo_categories**: TODO-カテゴリー中間テーブル
4. **attachments**: 添付ファイル

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