# API仕様書

## 概要
TODO管理アプリケーションのRESTful API仕様

## ベース情報
- **ベースURL**: `http://localhost:8080/api/v1`
- **データ形式**: JSON
- **文字エンコーディング**: UTF-8
- **認証**: 現在無効化（開発用）

## 共通仕様

### HTTPステータスコード
| コード | 説明 |
|--------|------|
| 200 | OK - 成功 |
| 201 | Created - 作成成功 |
| 204 | No Content - 削除成功 |
| 400 | Bad Request - リクエストエラー |
| 404 | Not Found - リソースが見つからない |
| 500 | Internal Server Error - サーバーエラー |

### エラーレスポンス形式
```json
{
  "code": "ERROR_CODE",
  "message": "エラーメッセージ",
  "details": {
    "field": "詳細情報"
  },
  "timestamp": "2024-01-01T00:00:00Z"
}
```

## エンドポイント一覧

### 1. TODO作成
```
POST /api/v1/todos
```

**リクエストボディ**:
```json
{
  "title": "サンプルTODO",
  "description": "詳細説明（任意）",
  "priority": "HIGH",
  "dueDate": "2024-12-31"
}
```

**レスポンス** (201 Created):
```json
{
  "id": 1,
  "title": "サンプルTODO",
  "description": "詳細説明（任意）",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31",
  "createdAt": "2024-01-01T09:00:00+09:00",
  "updatedAt": "2024-01-01T09:00:00+09:00"
}
```

### 2. TODO取得（ID指定）
```
GET /api/v1/todos/{id}
```

**パスパラメータ**:
- `id`: TODO ID (Long)

**レスポンス** (200 OK):
```json
{
  "id": 1,
  "title": "サンプルTODO",
  "description": "詳細説明",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-12-31",
  "createdAt": "2024-01-01T09:00:00+09:00",
  "updatedAt": "2024-01-01T09:00:00+09:00"
}
```

### 3. TODO一覧取得
```
GET /api/v1/todos
```

**クエリパラメータ**:
- `page`: ページ番号（デフォルト: 0）
- `size`: 1ページあたりの件数（デフォルト: 20）
- `sort`: ソート条件（デフォルト: createdAt,desc）

**レスポンス** (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "title": "サンプルTODO",
      "description": "詳細説明",
      "status": "TODO",
      "priority": "HIGH",
      "dueDate": "2024-12-31",
      "createdAt": "2024-01-01T09:00:00+09:00",
      "updatedAt": "2024-01-01T09:00:00+09:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "orderBy": "createdAt",
      "direction": "DESC"
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### 4. ステータス別TODO取得
```
GET /api/v1/todos/status/{status}
```

**パスパラメータ**:
- `status`: TODO, IN_PROGRESS, DONE

**レスポンス** (200 OK):
```json
[
  {
    "id": 1,
    "title": "進行中のTODO",
    "description": "詳細説明",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "dueDate": "2024-12-31",
    "createdAt": "2024-01-01T09:00:00+09:00",
    "updatedAt": "2024-01-01T10:00:00+09:00"
  }
]
```

### 5. TODO更新
```
PUT /api/v1/todos/{id}
```

**パスパラメータ**:
- `id`: TODO ID (Long)

**リクエストボディ**:
```json
{
  "title": "更新されたTODO",
  "description": "更新された詳細説明",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2024-12-25"
}
```

**レスポンス** (200 OK):
```json
{
  "id": 1,
  "title": "更新されたTODO",
  "description": "更新された詳細説明",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2024-12-25",
  "createdAt": "2024-01-01T09:00:00+09:00",
  "updatedAt": "2024-01-01T11:00:00+09:00"
}
```

### 6. TODO削除
```
DELETE /api/v1/todos/{id}
```

**パスパラメータ**:
- `id`: TODO ID (Long)

**レスポンス** (204 No Content):
レスポンスボディなし

## データモデル

### TodoStatus (Enum)
- `TODO`: 未着手
- `IN_PROGRESS`: 進行中
- `DONE`: 完了

### TodoPriority (Enum)
- `HIGH`: 高
- `MEDIUM`: 中
- `LOW`: 低

## バリデーション

### CreateTodoRequest
- `title`: 必須、最大255文字
- `description`: 任意、最大1000文字
- `priority`: 任意（デフォルト: MEDIUM）
- `dueDate`: 任意

### UpdateTodoRequest
- `title`: 必須、最大255文字
- `description`: 任意、最大1000文字
- `status`: 必須
- `priority`: 必須
- `dueDate`: 任意

## CORS設定
- **許可オリジン**: `http://localhost:3000`
- **許可メソッド**: GET, POST, PUT, DELETE, OPTIONS
- **許可ヘッダー**: すべて

## 使用例

### cURLサンプル

#### TODO作成
```bash
curl -X POST http://localhost:8080/api/v1/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "テストTODO",
    "description": "テスト用のTODO",
    "priority": "HIGH",
    "dueDate": "2024-12-31"
  }'
```

#### TODO一覧取得
```bash
curl -X GET "http://localhost:8080/api/v1/todos?page=0&size=10&sort=createdAt,desc"
```

#### TODO更新
```bash
curl -X PUT http://localhost:8080/api/v1/todos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "更新されたTODO",
    "description": "更新された説明",
    "status": "DONE",
    "priority": "LOW",
    "dueDate": "2024-12-25"
  }'
```

## 今後の機能拡張予定
1. **認証・認可**: JWT Bearer Token
2. **検索機能**: タイトル・説明での部分一致検索
3. **一括操作**: 複数TODO の一括更新・削除
4. **ファイル添付**: TODO へのファイル添付機能
5. **通知機能**: 期限間近の TODO 通知