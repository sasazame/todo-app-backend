# アーキテクチャ設計書

## 概要
本プロジェクトは、ヘキサゴナルアーキテクチャ（ポート&アダプターパターン）を採用したSpring Boot製TODOアプリケーションです。

## アーキテクチャ図
```
┌─────────────────────────────────────────────────────────┐
│                  Presentation Layer                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐    │
│  │ Controller  │ │ Request DTO │ │ Response DTO    │    │
│  │             │ │             │ │                 │    │
│  └─────────────┘ └─────────────┘ └─────────────────┘    │
└─────────────────────┬───────────────────────────────────┘
                      │ ↕ HTTP/JWT
┌─────────────────────┴───────────────────────────────────┐
│                 Security Layer                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐    │
│  │ Auth Filter │ │ JWT Service │ │ User Context    │    │
│  │             │ │             │ │                 │    │
│  └─────────────┘ └─────────────┘ └─────────────────┘    │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────┐
│                Application Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐    │
│  │ Service     │ │ Service DTO │ │ Mapper          │    │
│  │             │ │             │ │                 │    │
│  └─────────────┘ └─────────────┘ └─────────────────┘    │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────┐
│                   Domain Layer                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐    │
│  │ Model       │ │ Repository  │ │ Domain Service  │    │
│  │ (User,Todo) │ │ Interface   │ │                 │    │
│  └─────────────┘ └─────────────┘ └─────────────────┘    │
└─────────────────────┬───────────────────────────────────┘
                      │
┌─────────────────────┴───────────────────────────────────┐
│               Infrastructure Layer                      │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────┐    │
│  │ Entity      │ │ Repository  │ │ Security Config │    │
│  │ (JPA)       │ │ Impl        │ │                 │    │
│  └─────────────┘ └─────────────┘ └─────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

## レイヤー詳細

### 1. Presentation Layer
**責務**: 外部とのインターフェース
- **Controller**: HTTP リクエスト/レスポンス処理
- **Request/Response DTO**: データ転送オブジェクト
- **Mapper**: DTO ⇔ Domain Model 変換

**主要コンポーネント**:
- `TodoController`: TODO REST APIエンドポイント
- `AuthenticationController`: 認証APIエンドポイント
- `CreateTodoRequest`, `UpdateTodoRequest`: TODO関連DTO
- `RegisterRequest`, `LoginRequest`: 認証関連DTO
- `TodoResponse`, `AuthenticationResponse`: レスポンスDTO

### 2. Security Layer
**責務**: 認証・認可、セキュリティ制御
- **Authentication Filter**: JWTトークン検証
- **JWT Service**: トークン生成・検証
- **User Context**: セキュリティコンテキスト管理

**主要コンポーネント**:
- `JwtAuthenticationFilter`: JWT認証フィルター
- `JwtService`: JWT処理サービス
- `UserContextService`: ユーザーコンテキスト管理
- `CustomUserDetailsService`: Spring Securityユーザー詳細サービス

### 3. Application Layer
**責務**: ビジネスロジック、ユースケース実行
- **Service**: ビジネスルール実装
- **Transaction管理**: データ一貫性保証

**主要コンポーネント**:
- `TodoService`: TODOのCRUD操作とアクセス制御
- `AuthenticationService`: ユーザー登録・ログイン処理
- `UserContextService`: 認証ユーザー情報管理

### 4. Domain Layer
**責務**: 核となるビジネスルール、他の層に依存しない
- **Model**: ビジネス概念
- **Repository Interface**: データアクセスの抽象化

**主要コンポーネント**:
- `Todo`, `User`: ドメインモデル
- `TodoStatus`, `TodoPriority`: ドメインモデル（Enum）
- `TodoRepository`, `UserRepository`: リポジトリインターフェース

### 5. Infrastructure Layer
**責務**: 外部システムとの連携
- **Repository Implementation**: データアクセス実装
- **Entity**: データベーステーブルマッピング
- **Configuration**: インフラ設定

**主要コンポーネント**:
- `TodoEntity`, `UserEntity`: JPA エンティティ
- `TodoRepositoryImpl`, `UserRepositoryImpl`: リポジトリ実装
- `TodoJpaRepository`, `UserJpaRepository`: Spring Data JPA
- `SecurityConfig`: セキュリティ設定
- Flyway マイグレーション: データベーススキーマ管理

## 依存関係のルール
1. **内側の層は外側の層に依存しない**
2. **依存の方向**: Presentation → Security → Application → Domain ← Infrastructure
3. **Domain層は最も独立性が高い**
4. **Security層はPresentation層とApplication層の間で認証・認可を担当**

## データフロー
```
HTTP Request → Security Filter → Controller → Service → Repository Interface
       ↓              ↓               ↓           ↓              ↓
   JWT Token → Authentication → Response DTO ← Business Logic ← Repository Impl → Database
```

## セキュリティアーキテクチャ

### 認証フロー
```
1. ユーザー登録/ログイン → AuthenticationController
2. パスワード検証 → AuthenticationService  
3. JWT生成 → JwtService
4. トークン返却 → クライアント
```

### 認可フロー
```
1. APIリクエスト + JWT → JwtAuthenticationFilter
2. トークン検証 → JwtService
3. ユーザー情報設定 → SecurityContext
4. アクセス制御 → TodoService (所有者チェック)
```

## 設計原則
1. **依存性逆転の原則**: ドメイン層がインフラ層の詳細に依存しない
2. **単一責任の原則**: 各クラスは一つの責務のみ
3. **開放閉鎖の原則**: 拡張に開いていて、変更に閉じている

## テスト戦略

### テストピラミッド
```
        ┌─────────────────┐
        │ E2E/Integration │ ← 少数・高価値
        │      Tests      │
        ├─────────────────┤
        │  Integration    │ ← 中程度・API/DB
        │     Tests       │
        ├─────────────────┤
        │   Unit Tests    │ ← 多数・高速・安価
        │                 │
        └─────────────────┘
```

### テスト分類

#### 1. 単体テスト (Unit Tests)
- **対象**: Service層のビジネスロジック
- **ツール**: JUnit 5 + Mockito
- **カバレッジ目標**: 90%以上
- **例**: `TodoServiceTest`, `AuthenticationServiceTest`

#### 2. 統合テスト (Integration Tests)
- **対象**: エンドツーエンドのAPI動作
- **ツール**: SpringBootTest + MockMvc + H2
- **テスト内容**:
  - 認証・認可フロー
  - TODO CRUD操作とアクセス制御
  - HTTPステータスコード検証
- **例**: `AuthenticationIntegrationTest`, `TodoIntegrationTest`

#### 3. セキュリティテスト
- **対象**: 認証・認可機能
- **テスト内容**:
  - JWT トークン検証
  - アクセス制御 (403 Forbidden)
  - 認証なしアクセス (401 Unauthorized)

#### 4. データベーステスト
- **対象**: Repository層のデータアクセス
- **ツール**: @DataJpaTest + H2
- **テスト内容**: CRUD操作、クエリ検証

### テスト環境設定
- **データベース**: H2 In-Memory (テスト専用)
- **設定ファイル**: `application-test.yml`
- **マイグレーション**: 専用テスト用Flywayスクリプト
- **並列実行**: 各テストクラスが独立して実行可能

## 今後の拡張予定
1. **キャッシュ**: Redis, Spring Cache
2. **メッセージング**: RabbitMQ, Spring AMQP  
3. **監視**: Spring Boot Actuator, Micrometer
4. **検索**: Elasticsearch
5. **API仕様**: OpenAPI/Swagger