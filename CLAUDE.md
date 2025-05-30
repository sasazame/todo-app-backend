# Claude Code 協働開発ガイドライン

## プロジェクト概要
TODOアプリケーション - バックエンド API
- **技術スタック**: Spring Boot 3.3+, Java 21, PostgreSQL, Maven
- **アーキテクチャ**: ヘキサゴナルアーキテクチャ
- **目的**: 堅牢でスケーラブルなRESTful TODO管理API

## 開発フロー（重要）
```bash
# 1. 新機能開発時はfeatブランチを作成
git checkout -b feat/feature-name

# 2. 実装・テスト・コミット
git add . && git commit -m "feat: 機能の説明"

# 3. GitHubにプッシュしてPRを作成（宛先: sasazame）
git push origin feat/feature-name
gh pr create --title "機能タイトル" --body "詳細説明" --assignee sasazame
```

## コーディング規約

### Java
- Google Java Style Guide準拠
- メソッドは20行以内、単一責任
- 早期リターン、Optional活用
- Record使用でイミュータブルDTO

### Spring Boot
- コンストラクタインジェクション（`@RequiredArgsConstructor`）
- レイヤー責務分離:
  - Controller: リクエスト/レスポンス変換、バリデーション
  - Service: ビジネスロジック、トランザクション
  - Repository: データアクセスのみ
- DTOとEntity必須分離

### データベース
- Flyway使用、snake_case命名
- 外部キー制約必須、適切なインデックス

## コミット規約
```
<type>(<scope>): <subject>

<body>

🤖 Generated with [Claude Code](https://claude.ai/code)
Co-Authored-By: Claude <noreply@anthropic.com>
```

**タイプ**: feat, fix, docs, style, refactor, perf, test, chore, build, ci

## テスト方針
- 単体テスト: JUnit 5 + Mockito（Service層中心）
- 統合テスト: @SpringBootTest + TestContainers
- APIテスト: @WebMvcTest + MockMvc
- カバレッジ: 80%以上（Service層100%）

## アーキテクチャ構造
```
src/main/java/com/example/todoapp/
├── common/              # 共通コンポーネント
├── domain/              # ドメイン層（他に依存しない）
├── application/         # アプリケーション層
├── infrastructure/      # インフラ層
└── presentation/        # プレゼンテーション層
```

## API仕様
- ベースURL: `/api/v1`
- RESTful設計、適切なHTTPステータス
- CORS設定: localhost:3000許可
- エラーレスポンス統一形式

## セキュリティ
- 現在: 認証無効化（開発用）
- 本番: JWT認証・認可必須
- CORS、XSS、SQLインジェクション対策

## Claude Codeへの依頼テンプレート
```markdown
## 実装したい機能
[具体的な機能説明]

## 現在の状況
[関連ファイル、既存実装]

## 期待する結果
[APIレスポンス例、動作説明]

## 制約事項
[パフォーマンス要件、セキュリティ考慮]
```

## 開発時チェックリスト
- [ ] featブランチで作業
- [ ] レイヤー責務分離
- [ ] DTOとEntity分離
- [ ] 適切な例外処理
- [ ] バリデーション実装
- [ ] ログ出力適切
- [ ] テスト作成
- [ ] PR作成（assignee: sasazame）

## 環境・設定
- Java 21, Maven 3.8+, PostgreSQL 16+
- application.yml: 詳細は`docs/`参照
- DB接続: localhost:5432/todoapp (todoapp/todoapp)

## 重要な実装パターン
1. **Repository**: インターフェース/実装分離
2. **Service**: `@Transactional`でトランザクション管理
3. **Controller**: `@Valid`でバリデーション
4. **Exception**: グローバルハンドラー使用
5. **DTO**: Recordで不変オブジェクト

このファイルはClaude Codeが効率的に作業するための簡潔なガイドライン。
詳細な設計情報は`README.md`および`docs/`フォルダーを参照。