# TODO App Backend

Spring Boot + PostgreSQL で構築されたTODOアプリケーションのバックエンドAPI

## 🚀 プロジェクト概要

### 技術スタック
- **Java**: 21 (OpenJDK)
- **フレームワーク**: Spring Boot 3.3.7
- **データベース**: PostgreSQL 16+
- **ビルドツール**: Maven 3.8+
- **アーキテクチャ**: ヘキサゴナルアーキテクチャ（ポート&アダプター）

### 主な機能
- ✅ TODOの CRUD 操作（作成・取得・更新・削除）
- ✅ ステータス管理（TODO/進行中/完了）
- ✅ 優先度設定（高/中/低）
- ✅ 期限日設定
- ✅ ページネーション対応
- ✅ RESTful API 設計
- ✅ バリデーション
- ✅ グローバル例外ハンドリング

## 📋 目次
1. [クイックスタート](#クイックスタート)
2. [環境構築](#環境構築)
3. [API 仕様](#api-仕様)
4. [開発ガイド](#開発ガイド)
5. [テスト](#テスト)
6. [設計資料](#設計資料)

## ⚡ クイックスタート

### 前提条件
- Java 21+
- Maven 3.8+
- PostgreSQL 16+

### 起動手順
```bash
# 1. リポジトリをクローン
git clone https://github.com/sasazame/todo-app-backend.git
cd todo-app-backend

# 2. データベース設定
sudo -u postgres psql -c "CREATE DATABASE todoapp;"
sudo -u postgres psql -c "CREATE USER todoapp WITH ENCRYPTED PASSWORD 'todoapp';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE todoapp TO todoapp;"

# 3. アプリケーション起動
mvn spring-boot:run

# 4. 動作確認
curl http://localhost:8080/api/v1/todos
```

アプリケーションは http://localhost:8080 で起動します。

## 🛠️ 環境構築

### データベースセットアップ
```bash
# PostgreSQL インストール（Ubuntu/Debian）
sudo apt update
sudo apt install postgresql postgresql-contrib

# データベース・ユーザー作成
sudo -u postgres psql << EOF
CREATE DATABASE todoapp;
CREATE USER todoapp WITH ENCRYPTED PASSWORD 'todoapp';
GRANT ALL PRIVILEGES ON DATABASE todoapp TO todoapp;
ALTER DATABASE todoapp OWNER TO todoapp;
\q
EOF
```

### 設定ファイル
主要な設定は `src/main/resources/application.yml` に記載:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/todoapp
    username: todoapp
    password: todoapp
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

## 📡 API 仕様

### ベース URL
```
http://localhost:8080/api/v1
```

### 主要エンドポイント
| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| POST | `/todos` | TODO作成 |
| GET | `/todos` | TODO一覧取得（ページング） |
| GET | `/todos/{id}` | TODO取得（ID指定） |
| GET | `/todos/status/{status}` | ステータス別TODO取得 |
| PUT | `/todos/{id}` | TODO更新 |
| DELETE | `/todos/{id}` | TODO削除 |

### リクエスト例
```bash
# TODO作成
curl -X POST http://localhost:8080/api/v1/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "プロジェクト完了",
    "description": "最終レビューと提出",
    "priority": "HIGH",
    "dueDate": "2024-12-31"
  }'

# TODO一覧取得
curl "http://localhost:8080/api/v1/todos?page=0&size=10&sort=createdAt,desc"
```

詳細なAPI仕様は [docs/API.md](docs/API.md) を参照してください。

## 👨‍💻 開発ガイド

### ブランチ戦略
```bash
# 新機能開発
git checkout -b feat/feature-name
# 実装・テスト・コミット
git commit -m "feat: 新機能の説明"
# プルリクエスト作成
git push origin feat/feature-name
gh pr create --assignee sasazame
```

### コーディング規約
- **Java**: Google Java Style Guide
- **コミットメッセージ**: Conventional Commits
- **テスト**: JUnit 5 + Mockito

### プロジェクト構造
```
src/main/java/com/example/todoapp/
├── common/              # 共通コンポーネント
│   ├── config/         # 設定クラス
│   ├── exception/      # 例外ハンドリング
│   └── util/           # ユーティリティ
├── domain/              # ドメイン層
│   ├── model/          # ドメインモデル
│   └── repository/     # リポジトリインターフェース
├── application/         # アプリケーション層
│   └── service/        # ビジネスロジック
├── infrastructure/      # インフラストラクチャ層
│   └── persistence/    # データアクセス
└── presentation/        # プレゼンテーション層
    ├── controller/     # REST コントローラー
    └── dto/            # データ転送オブジェクト
```

## 🧪 テスト

### テスト実行
```bash
# 全テスト実行
mvn test

# 統合テスト実行
mvn verify

# カバレッジレポート生成
mvn test jacoco:report
```

### テスト構成
- **単体テスト**: Service 層のビジネスロジック
- **統合テスト**: Repository 層のデータアクセス
- **API テスト**: Controller 層のエンドポイント

## 📚 設計資料

### 詳細ドキュメント
- [アーキテクチャ設計](docs/ARCHITECTURE.md) - システム全体の設計思想
- [データベース設計](docs/DATABASE.md) - DB スキーマと設計方針
- [API 仕様書](docs/API.md) - 詳細な API ドキュメント

### アーキテクチャ概要
```
┌─────────────────┐
│ Presentation    │ ← REST API, DTO
├─────────────────┤
│ Application     │ ← ビジネスロジック
├─────────────────┤
│ Domain          │ ← ドメインモデル
├─────────────────┤
│ Infrastructure  │ ← データアクセス, 外部連携
└─────────────────┘
```

## 🔧 ツール・ライブラリ

### 主要依存関係
- **Spring Boot Starter Web** - REST API
- **Spring Boot Starter Data JPA** - データアクセス
- **Spring Boot Starter Security** - セキュリティ
- **Spring Boot Starter Validation** - バリデーション
- **PostgreSQL Driver** - データベース接続
- **Flyway** - データベースマイグレーション
- **Lombok** - ボイラープレートコード削減

### 開発ツール
- **Spring Boot DevTools** - ホットリロード
- **Spring Boot Actuator** - 監視・メトリクス

## 🚧 今後の開発予定

### 近期予定
- [ ] JWT 認証・認可機能
- [ ] カテゴリー機能
- [ ] 検索・フィルタリング機能
- [ ] ファイル添付機能

### 中長期予定
- [ ] キャッシュ機能（Redis）
- [ ] 通知機能
- [ ] 一括操作 API
- [ ] OpenAPI ドキュメント自動生成

## 📝 ライセンス

このプロジェクトは MIT ライセンスの下で公開されています。

## 🤝 貢献

1. このリポジトリをフォーク
2. feature ブランチを作成 (`git checkout -b feat/amazing-feature`)
3. 変更をコミット (`git commit -m 'feat: 素晴らしい機能を追加'`)
4. ブランチにプッシュ (`git push origin feat/amazing-feature`)
5. プルリクエストを作成

## 📞 サポート

質問や問題がある場合は、[Issues](https://github.com/sasazame/todo-app-backend/issues) を作成してください。

---

**開発者**: sasazame  
**最終更新**: 2024年5月