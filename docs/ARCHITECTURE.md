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
│  │ (Enum)      │ │ Interface   │ │                 │    │
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
- `TodoController`: REST APIエンドポイント
- `CreateTodoRequest`, `UpdateTodoRequest`: リクエストDTO
- `TodoResponse`: レスポンスDTO

### 2. Application Layer
**責務**: ビジネスロジック、ユースケース実行
- **Service**: ビジネスルール実装
- **Transaction管理**: データ一貫性保証

**主要コンポーネント**:
- `TodoService`: TODOのCRUD操作とビジネスロジック

### 3. Domain Layer
**責務**: 核となるビジネスルール、他の層に依存しない
- **Model**: ビジネス概念
- **Repository Interface**: データアクセスの抽象化

**主要コンポーネント**:
- `TodoStatus`, `TodoPriority`: ドメインモデル（Enum）
- `TodoRepository`: リポジトリインターフェース

### 4. Infrastructure Layer
**責務**: 外部システムとの連携
- **Repository Implementation**: データアクセス実装
- **Entity**: データベーステーブルマッピング
- **Configuration**: インフラ設定

**主要コンポーネント**:
- `TodoEntity`: JPA エンティティ
- `TodoRepositoryImpl`: リポジトリ実装
- `TodoJpaRepository`: Spring Data JPA
- `SecurityConfig`: セキュリティ設定

## 依存関係のルール
1. **内側の層は外側の層に依存しない**
2. **依存の方向**: Presentation → Application → Domain ← Infrastructure
3. **Domain層は最も独立性が高い**

## データフロー
```
HTTP Request → Controller → Service → Repository Interface
                   ↓              ↓            ↓
             Response DTO ← Business Logic ← Repository Impl → Database
```

## 設計原則
1. **依存性逆転の原則**: ドメイン層がインフラ層の詳細に依存しない
2. **単一責任の原則**: 各クラスは一つの責務のみ
3. **開放閉鎖の原則**: 拡張に開いていて、変更に閉じている

## 今後の拡張予定
1. **認証・認可**: Spring Security + JWT
2. **キャッシュ**: Redis, Spring Cache
3. **メッセージング**: RabbitMQ, Spring AMQP
4. **監視**: Spring Boot Actuator, Micrometer