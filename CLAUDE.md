# Claude Code 協働開発ガイドライン

## プロジェクト概要
TODOアプリケーション - バックエンド
- **リポジトリ**: todo-app-backend
- **技術スタック**: Spring Boot 3.3+, Java 21, PostgreSQL, Maven
- **目的**: 堅牢でスケーラブルなTODO管理APIの提供

## 開発ルール

### 1. コーディング規約

#### Java
- Google Java Style Guideに準拠
- クラス名はPascalCase（例: `TodoController`）
- メソッド名、変数名はcamelCase（例: `getTodoById`）
- 定数はUPPER_SNAKE_CASE（例: `MAX_RETRY_COUNT`）
- パッケージ名は小文字（例: `com.example.todoapp`）
- メソッドは短く（20行以内を目安）、単一責任の原則を守る
- 早期リターンを活用してネストを減らす
- Optional を活用してnullを避ける
- Record を活用してイミュータブルなDTOを作成

#### Spring Boot
- レイヤードアーキテクチャを採用（Controller → Service → Repository）
- DIは必ずコンストラクタインジェクション（`@RequiredArgsConstructor`を活用）
- `@Autowired`は使用禁止
- 各レイヤーの責務:
  - Controller: リクエスト/レスポンスの変換、バリデーション
  - Service: ビジネスロジック、トランザクション管理
  - Repository: データアクセスのみ
- DTOとEntityは必ず分離（MapStructでマッピング）
- `@Value`でプロパティ注入する際は`@ConfigurationProperties`を優先

#### データベース
- マイグレーションツール: Flyway
- 命名規則: snake_case
- 外部キーには必ず制約を設定
- インデックスは適切に設定

### 2. コミットメッセージ規約（Conventional Commits）
```
<type>(<scope>): <subject>

<body>

<footer>
```

**タイプ**:
- `feat`: 新機能（エンドポイント追加、新規サービス等）
- `fix`: バグ修正
- `docs`: ドキュメントのみの変更
- `style`: コードの意味に影響しない変更（フォーマット等）
- `refactor`: バグ修正や機能追加を伴わないコード変更
- `perf`: パフォーマンス改善
- `test`: テストの追加・修正
- `chore`: ビルドプロセスやツールの変更
- `build`: ビルド設定やライブラリの更新
- `ci`: CI/CD設定の変更

**コミット粒度の原則**:
- 1つのコミットは1つの論理的な変更単位
- マイグレーションファイルは別コミット
- 自動生成されたコードは別コミット
- レビュー可能な大きさ（差分300行以内を目安）

**良い例**:
```
feat(todo): バッチ更新エンドポイントを追加

PUT /api/v1/todos/batch を実装。
複数のTODOを一括で更新可能。

実装内容:
- TodoControllerにbatchUpdateメソッドを追加
- TodoServiceにトランザクション処理を実装
- 楽観的ロックで同時更新を制御

Closes #12
```

**悪い例**:
```
feat: いろいろ修正

APIの修正とDBスキーマ変更とテスト追加
```

### 3. ブランチ戦略（GitHub Flow）
- `main`: 本番環境にデプロイ可能な状態を維持
- `feature/*`: 新機能開発（例: `feature/batch-update-api`）
- `fix/*`: バグ修正（例: `fix/null-pointer-exception`）
- `refactor/*`: リファクタリング（例: `refactor/service-layer-optimization`）

### 4. Claude Codeへの依頼方法

**依頼テンプレート**:
```markdown
## 実装したい機能
[機能の概要を具体的に記載]

## 現在の状況
[関連するファイルや既存の実装を説明]

## 期待する結果
[APIのレスポンス例やDB変更内容]

## 制約事項
[守るべきルールや考慮事項]
```

**効果的な依頼のポイント**:
- 具体的なエンドポイントとHTTPメソッドを明記
- リクエスト/レスポンスのJSON例を提示
- パフォーマンス要件がある場合は明記

### 5. コードレビューポイント
- [ ] SOLID原則が守られているか
- [ ] 適切な例外処理が実装されているか
- [ ] トランザクション境界が適切か
- [ ] N+1問題が発生していないか
- [ ] セキュリティ（SQLインジェクション、XSS等）への配慮
- [ ] 適切なHTTPステータスコードを返しているか
- [ ] ログ出力が適切か

### 6. テスト方針

#### テスト戦略
- **単体テスト**: JUnit 5 + Mockito
  - Service層のビジネスロジック
  - Utilityクラス
  - カスタムバリデーター
- **統合テスト**: @SpringBootTest + TestContainers
  - Repository層（実際のDB接続）
  - 複数コンポーネントの連携
- **APIテスト**: @WebMvcTest + MockMvc
  - Controllerのエンドポイント
  - リクエスト/レスポンスの検証

#### テスト設計原則
- AAA パターン（Arrange, Act, Assert）を使用
- テストメソッド名は日本語で記述可（`@DisplayName`を活用）
- テストデータはTestFixtureクラスで管理
- `@ParameterizedTest`で境界値テスト
- DBテストは`@Sql`で初期データ投入

#### カバレッジ目標
- 全体: 80%以上
- Service層: 100%
- Controller層: 90%以上
- Repository層: カスタムクエリのみ
- Entity/DTO: getter/setterは除外

#### テストの配置
```
src/test/java/com/example/todoapp/
├── controller/
│   └── TodoControllerTest.java
├── service/
│   └── TodoServiceTest.java
├── repository/
│   └── TodoRepositoryTest.java
├── integration/
│   └── TodoIntegrationTest.java
└── fixture/
    └── TodoFixture.java
```

## 技術仕様

### 使用技術スタック
```json
{
  "framework": "Spring Boot 3.3+",
  "language": "Java 21",
  "buildTool": "Maven 3.8+",
  "database": "PostgreSQL 16+",
  "migration": "Flyway",
  "testing": "JUnit 5, Mockito, TestContainers",
  "documentation": "OpenAPI 3.0 (Springdoc)",
  "security": "Spring Security + JWT",
  "monitoring": "Spring Boot Actuator"
}
```

### アーキテクチャパターン
```
src/main/java/com/example/todoapp/
├── TodoAppApplication.java
├── common/              # 共通コンポーネント
│   ├── config/          # 設定クラス
│   ├── exception/       # 例外クラス・ハンドラー
│   ├── validation/      # カスタムバリデーター
│   └── util/            # ユーティリティ
├── domain/              # ドメイン層
│   ├── model/           # ドメインモデル
│   └── repository/      # リポジトリインターフェース
├── application/         # アプリケーション層
│   ├── service/         # ビジネスロジック
│   └── dto/             # サービス層のDTO
├── infrastructure/      # インフラ層
│   ├── persistence/     # JPA実装
│   │   ├── entity/      # JPAエンティティ
│   │   └── repository/  # リポジトリ実装
│   └── security/        # セキュリティ設定
└── presentation/        # プレゼンテーション層
    ├── controller/      # REST API
    ├── dto/             # API用DTO
    │   ├── request/     # リクエストDTO
    │   └── response/    # レスポンスDTO
    └── mapper/          # DTO-Entity マッパー
```

#### パッケージ設計の原則
- ドメイン層は他の層に依存しない
- 依存方向: Presentation → Application → Domain ← Infrastructure
- 各層のDTOは分離（過度な共有を避ける）

### API設計規約
- RESTful設計原則に準拠
- ベースURL: `/api/v1`
- 命名規則:
  - 複数形のリソース名（例: `/todos`）
  - ケバブケース使用（例: `/todo-categories`）
- HTTPメソッド:
  - GET: 取得
  - POST: 作成
  - PUT: 全体更新
  - PATCH: 部分更新
  - DELETE: 削除

**レスポンス形式**:
```java
// 成功時
{
  "data": {
    // レスポンスデータ
  },
  "meta": {
    "timestamp": "2024-01-01T00:00:00Z",
    "version": "1.0"
  }
}

// エラー時
{
  "error": {
    "code": "TODO_NOT_FOUND",
    "message": "指定されたTODOが見つかりません",
    "details": {
      "todoId": 123
    }
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

### エラーハンドリング方針

#### 例外の分類
1. **ビジネス例外**（チェック例外）
   - `BusinessException`を継承
   - 回復可能なエラー（バリデーションエラー等）
   - HTTPステータス: 4xx

2. **システム例外**（非チェック例外）
   - `SystemException`を継承
   - 回復不可能なエラー（DB接続エラー等）
   - HTTPステータス: 5xx

#### 実装方針
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(TodoNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(TodoNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of("TODO_NOT_FOUND", e.getMessage()));
    }
    
    // 他の例外ハンドリング...
}
```

#### エラーレスポンス設計
- エラーコードは定数クラスで一元管理
- メッセージは国際化対応（MessageSource使用）
- 開発環境のみスタックトレース含む
- 本番環境ではトラッキングIDを付与

## プロジェクト固有ルール

### TODOアプリの機能要件
1. **基本機能**
   - TODOの作成、読取、更新、削除（CRUD）
   - TODOのステータス管理（未着手、進行中、完了）
   - 優先度設定（高、中、低）

2. **応用機能**
   - カテゴリー分類
   - 期限設定とリマインダー
   - 検索・フィルタリング・ソート
   - ページネーション
   - 一括操作（複数選択での状態変更等）

3. **非機能要件**
   - レスポンスタイム: 200ms以内（95%ile）
   - 同時接続数: 1000ユーザー
   - データ保持期間: 無制限
   - 監査ログの記録

### 将来的な技術スタック置き換えを考慮した設計指針
1. **疎結合な設計**
   - インターフェースベースの実装
   - 依存性逆転の原則（DIP）の適用
   - ヘキサゴナルアーキテクチャの概念を意識

2. **抽象化レイヤー**
   - リポジトリパターンでデータアクセスを抽象化
   - サービス層でビジネスロジックを集約
   - 外部サービスとの通信はアダプターパターン

3. **マイグレーション容易性**
   - ドメインモデルの独立性を保つ
   - フレームワーク固有のアノテーションは最小限に
   - 設定の外部化（application.yml）

## 開発開始時の確認事項
```bash
# 環境確認
java --version  # 21以上
mvn --version   # 3.8以上

# PostgreSQL起動確認
sudo systemctl status postgresql

# ビルド
mvn clean install

# アプリケーション起動
mvn spring-boot:run

# テスト実行
mvn test

# 統合テスト実行
mvn verify

# コードフォーマット
mvn spotless:apply
```

## データベース設定
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/todoapp
    username: todoapp
    password: todoapp
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  flyway:
    enabled: true
    locations: classpath:db/migration
```

## Claude Codeとの連携
このファイルはClaude Codeが参照し、プロジェクトのコンテキストを理解するために使用されます。
開発方針に変更がある場合は、このファイルを更新してください。

## セキュリティ考慮事項

### 認証・認可
- JWT Bearer Tokenによる認証
- Spring SecurityのMethodSecurityで認可制御
- リフレッシュトークンの実装
- トークンの有効期限管理

### API保護
- CORS設定（許可オリジンの制限）
- APIレート制限（Spring Bucket4j）
- リクエストサイズ制限
- タイムアウト設定

### データ保護
- パスワードはBCryptでハッシュ化
- 機密情報は環境変数で管理（Spring Vault検討）
- SQLインジェクション対策（JPA使用、カスタムクエリは@Paramアノテーション）
- XSS対策（`@Valid`による入力検証）

### 監査・ログ
- 認証失敗のログ記録
- 重要操作の監査ログ（AOP実装）
- 個人情報のマスキング
- ログローテーション設定

## 開発ナレッジ（2025年5月実装時）

### 環境構築
1. **必要なツールのバージョン**
   - Java 21 (OpenJDK)
   - Maven 3.8+
   - PostgreSQL 16+
   - Node.js 18+ (フロントエンド連携用)

2. **PostgreSQLセットアップ**
   ```bash
   # データベースとユーザー作成
   sudo -u postgres psql -c "CREATE DATABASE todoapp;"
   sudo -u postgres psql -c "CREATE USER todoapp WITH ENCRYPTED PASSWORD 'todoapp';"
   sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE todoapp TO todoapp;"
   sudo -u postgres psql -c "ALTER DATABASE todoapp OWNER TO todoapp;"
   ```

3. **Spring Initializr設定**
   - Dependencies: web, data-jpa, postgresql, flyway, validation, security, actuator, devtools, lombok
   - Java Version: 21
   - Spring Boot: 3.3.7

### 実装時の注意点

1. **Lombok使用時**
   - pom.xmlにLombok依存関係を追加（Spring Initializrで選択しなかった場合）
   - `@RequiredArgsConstructor`でコンストラクタインジェクション

2. **application.yml設定**
   - Flywayの`baseline-on-migrate: true`で既存DBへの適用を可能に
   - JPAの`open-in-view: false`でN+1問題を防ぐ
   - Hibernateの`ddl-auto: validate`で予期しないスキーマ変更を防ぐ

3. **ヘキサゴナルアーキテクチャ実装**
   - domain層は他の層に依存しない
   - インターフェースと実装を分離（TodoRepository/TodoRepositoryImpl）
   - DTOは各層で分離（過度な共有を避ける）

4. **開発時の一時的な対応**
   - Spring Securityを一時的に無効化（SecurityConfig）
   - 本番環境では必ず認証・認可を実装

### トラブルシューティング

1. **Gitコミット時のエラー**
   - ユーザー設定が必要: `git config user.email/user.name`
   - リポジトリごとの設定は`--global`を省略

2. **Flyway実行エラー**
   - ファイル名規則: `V<version>__<description>.sql`
   - 既存DBの場合は`baseline-on-migrate: true`を設定

3. **CORS関連**
   - `@CrossOrigin`とSecurityConfigの両方で設定
   - 開発時はlocalhost:3000を許可

### パフォーマンス考慮事項
- HikariCPのコネクションプール設定を適切に調整
- JPAのN+1問題を回避（適切なfetch戦略）
- ページネーションでメモリ使用量を制御

### 今後の改善ポイント
1. JWT認証の実装
2. MapStructによるDTO-Entityマッピング
3. TestContainersを使った統合テスト
4. OpenAPI (Swagger)ドキュメント生成
5. 監査ログ（Spring AOP）
6. キャッシュ戦略（Spring Cache）