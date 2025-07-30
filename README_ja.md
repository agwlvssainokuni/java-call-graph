# Java コールグラフ解析ツール

[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://openjdk.java.net/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen)](https://spring.io/projects/spring-boot)
[![SootUp](https://img.shields.io/badge/SootUp-2.0.0-blue)](https://github.com/soot-oss/SootUp)
[![Gradle](https://img.shields.io/badge/Gradle-8.14.3-brightgreen)](https://gradle.org/)

SootUp 2.0.0フレームワークとSpring Bootを使用したJavaアプリケーションの静的コールグラフ解析のための高機能コマンドラインツールです。

## 機能

- **複数の解析アルゴリズム**: CHA（クラス階層解析）とRTA（高速型解析）
- **自動インタフェース解決**: SootUp 2.0.0がインタフェース呼び出しを自動的に処理
- **柔軟なフィルタリング**: FQCNベースのクラス包含と除外
- **カスタムエントリポイント**: 解析開始点としてカスタムメソッドを指定可能
- **柔軟な入力サポート**: JARファイル、クラスファイル、ディレクトリ
- **複数の出力形式**: TXT（人間可読）、CSV（データ解析）、JSON（プログラム処理）、DOT（可視化）
- **重複除去**: 挿入順序を維持しながらコールエッジの重複を除去
- **プロフェッショナルCLI**: Spring Bootで構築された堅牢なコマンドラインインタフェース

## クイックスタート

### 前提条件

- Java 21以上
- Gradle 8.14.3以上（Gradle Wrapperが含まれています）

### ビルドと実行

```bash
# リポジトリをクローン
git clone https://github.com/agwlvssainokuni/java-call-graph.git
cd java-call-graph

# プロジェクトをビルド
./gradlew build

# 基本的な解析を実行
./gradlew bootRun --args="your-application.jar"

# 実行可能JARを作成
./gradlew bootJar
java -jar build/libs/java-call-graph-*.jar your-application.jar
```

## 使用例

### 基本的な解析
```bash
# JARファイルを解析
./gradlew bootRun --args="application.jar"

# 詳細情報付きの冗長出力
./gradlew bootRun --args="--verbose application.jar"
```

### クラスフィルタリングと除外
```bash
# 特定のクラス前置詞（FQCN）に解析を集中
./gradlew bootRun --args="--include=com.example application.jar"

# 複数のクラス前置詞
./gradlew bootRun --args="--include=com.example,org.mycompany application.jar"

# FQCN前置詞で特定のクラスを除外
./gradlew bootRun --args="--exclude=com.example.test application.jar"

# 複数のクラス/パッケージを除外
./gradlew bootRun --args="--exclude=com.example.test,org.junit application.jar"

# フィルタリングと除外の組み合わせ
./gradlew bootRun --args="--include=com.example --exclude=com.example.test application.jar"
```

### アルゴリズムの選択
```bash
# RTAアルゴリズムを使用（インタフェース解決に推奨）
./gradlew bootRun --args="--algorithm=rta --include=com.example application.jar"

# CHAアルゴリズムを使用（高速だが精度が低い）
./gradlew bootRun --args="--algorithm=cha application.jar"
```

### カスタムエントリポイント
```bash
# カスタムエントリポイントメソッドを指定
./gradlew bootRun --args="--entry=Controller.handleRequest application.jar"

# 複数のエントリポイント
./gradlew bootRun --args="--entry=Controller.handleRequest,Service.processData application.jar"

# エントリポイントのワイルドカードパターン
./gradlew bootRun --args="--entry=*Controller.handle* application.jar"

# 複数のワイルドカードパターン
./gradlew bootRun --args="--entry=*Controller.*,*Service.process* application.jar"
```

### 出力形式
```bash
# スプレッドシート解析用のCSV形式
./gradlew bootRun --args="--format=csv --output=callgraph.csv application.jar"

# プログラム処理用のJSON形式
./gradlew bootRun --args="--format=json --output=callgraph.json application.jar"

# Graphviz可視化用のDOT形式
./gradlew bootRun --args="--format=dot --output=callgraph.dot application.jar"
dot -Tpng callgraph.dot -o callgraph.png
```

## コマンドラインオプション

| オプション | 説明 | デフォルト |
|-----------|------|-----------|
| `--algorithm=<algo>` | 解析アルゴリズム: `cha`, `rta` | `cha` |
| `--entry=<method>` | エントリポイントメソッド（ClassName.methodName形式、ワイルドカード * 対応） | mainメソッド |
| `--include=<class>` | FQCN前置詞でクラスを包含（カンマ区切り） | 全クラス |
| `--exclude=<class>` | FQCN前置詞でクラスを除外（カンマ区切り） | なし |
| `--exclude-jdk` | JDKクラスを解析から除外 | `false` |
| `--output=<file>` | コールグラフの出力ファイル | 標準出力 |
| `--format=<format>` | 出力形式: `txt`, `csv`, `json`, `dot` | `txt` |
| `--quiet` | 標準出力を抑制 | `false` |
| `--verbose` | 詳細情報を表示 | `false` |
| `--help` | ヘルプメッセージを表示 | - |

## アーキテクチャ

### コアコンポーネント

- **Main.java**: 適切なコンテキスト管理を持つSpring Boot CLIエントリポイント
- **CallGraphRunner.java**: CLI引数処理と解析オーケストレーション
- **output/OutputFormatter.java**: 複数形式出力生成（TXT、CSV、JSON、DOT）
- **output/Format.java**: 出力形式enum

### インタフェースベースアーキテクチャ

- **analyze/** パッケージ: コア解析インタフェースとデータ転送オブジェクト
  - `CallGraphAnalyzer.java`: 契約を定義する解析インタフェース
  - `Algorithm.java`: 解析アルゴリズムenum
  - `AnalysisResult.java`, `ClassInfo.java`, `MethodInfo.java`, `CallEdgeInfo.java`: データレコード
- **output/** パッケージ: 出力フォーマットと形式定義
  - `OutputFormatter.java`: 複数形式出力生成
  - `Format.java`: 出力形式enum
- **sootup/** パッケージ: SootUp固有の実装
  - `SootUpAnalyzer.java`: SootUp統合とコールグラフ解析エンジン
- **依存性注入**: Spring Bootがインタフェースから実装への結合を管理

### 解析エンジン

SootUp 2.0.0上に構築された高機能な機能:

- **静的解析**: 精度/パフォーマンスのトレードオフに応じた複数のアルゴリズム
- **インタフェース解決**: SootUpがインタフェース呼び出しを自動処理（手動拡張不要）
- **コールグラフAPI**: 適切なコールエッジ抽出のため`getSourceMethodSignature()`と`getTargetMethodSignature()`を使用
- **重複除去**: `LinkedHashSet`が挿入順序を保持しながら重複するコールエッジを除去
- **ビュー管理**: 入力場所による適切なSootUp JavaView設定
- **エントリポイント処理**: 自動mainメソッド検出とワイルドカードパターン対応のカスタムエントリポイントサポート（Javaクラス/メソッド名の$文字を含む適切な正規表現エスケープ処理）

## サポートされる入力タイプ

- **JARファイル** (`.jar`): 標準的なJavaアーカイブファイル
- **クラスファイル** (`.class`): 個別のコンパイル済みJavaクラス
- **ディレクトリ**: クラスファイルを再帰的にスキャン
- **WARファイル** (`.war`): Webアプリケーションアーカイブ *(計画中)*

## 出力形式

### TXT形式
コールエッジとクラス情報を表示する人間可読なテキスト出力:
```
=== Call Graph Analysis Results ===

Call Graph (5 edges):
  com.example.Main.main -> com.example.Service.process
  com.example.Service.process -> com.example.Repository.save
  ...

Classes (3):
  com.example.Main
  com.example.Service
  com.example.Repository
```

### CSV形式
スプレッドシート解析に適した構造化コールエッジデータ（冗長モードに関係なくコールエッジのみ出力）:
```csv
source_class,source_method,target_class,target_method
"com.example.Main","main","com.example.Service","process"
"com.example.Service","process","com.example.Repository","save"
```

### JSON形式
プログラム処理とAPI統合のための構造化JSON出力:
```json
{
  "callEdges": [
    {
      "sourceClass": "com.example.Main",
      "sourceMethod": "main",
      "targetClass": "com.example.Service",
      "targetMethod": "process"
    },
    {
      "sourceClass": "com.example.Service",  
      "sourceMethod": "process",
      "targetClass": "com.example.Repository",
      "targetMethod": "save"
    }
  ]
}
```

### DOT形式
視覚的なコールグラフ生成のためのGraphviz互換形式（2行ラベル表示）:
```dot
digraph CallGraph {
  rankdir=LR;
  node [shape=box, style=rounded];
  
  "com.example.Main.main" [label="com.example.Main\nmain"];
  "com.example.Service.process" [label="com.example.Service\nprocess"];
  "com.example.Repository.save" [label="com.example.Repository\nsave"];
  
  "com.example.Main.main" -> "com.example.Service.process";
  "com.example.Service.process" -> "com.example.Repository.save";
}
```

## フィルタリングと除外

このツールは集中的な解析のための柔軟なフィルタリングオプションを提供します:

- **クラス包含**: `--include=<class>`を使用して特定のクラス前置詞（FQCN）に集中
- **クラス除外**: FQCNベースの除外に`--exclude=<class>`を使用（特定のクラスとパッケージ前置詞の両方をサポート）
- **フィルタ優先度**: 除外フィルタが最初にチェックされ、その後包含フィルタが適用されます
- **JDK除外**: 標準ライブラリクラスを除去するために`--exclude-jdk`を使用
- **アルゴリズム推奨**: より良いインタフェース呼び出し解決のためRTAアルゴリズム（`--algorithm=rta`）を使用

使用例の組み合わせ:
```bash
# ビジネスロジックに集中、テストを除外
./gradlew bootRun --args="--include=com.example --exclude=com.example.test spring-app.jar"

# 複数のテストフレームワークを除外
./gradlew bootRun --args="--exclude=org.junit,org.mockito,com.example.Mock spring-app.jar"
```

## ビルドシステム

- **Gradle 8.14.3** Gradle Wrapper付き
- **Java 21** UTF-8エンコーディング付きツールチェーン
- **Spring Boot BOM** 依存関係管理用
- **コード品質**: 非推奨警告と未チェック操作警告を有効化

## 依存関係

- **Spring Boot 3.5.4**: コアフレームワークとCLIインフラ
- **SootUp 2.0.0**: 静的解析エンジン
  - `sootup.core`: コア解析機能
  - `sootup.java.core`: Java固有のクラス
  - `sootup.java.bytecode.frontend`: バイトコードフロントエンド
  - `sootup.callgraph`: コールグラフアルゴリズム
- **Jakarta Annotations**: 標準アノテーションサポート

## 開発

### ビルド
```bash
./gradlew build
```

### テスト
```bash
./gradlew test
```

### 配布版作成
```bash
./gradlew bootJar
```

### 自己解析テスト
ツール自体でのテスト:
```bash
./gradlew build
./gradlew bootRun --args="--verbose build/libs/java-call-graph-plain.jar"
```

## コード品質基準

- Java 21言語機能（レコード、switch式、var）
- モダンなSootUp API（2.0.0互換メソッド）
- 命令型ループよりもStream APIと関数型プログラミングスタイルを優先
- try-with-resourcesによる適切なリソース管理
- 包括的なエラーハンドリングとログ記録
- FQCN使用よりもimport文を優先
- 未使用メソッドパラメータを削除
- if-presentパターンよりもOptional.ifPresent()を優先

## ライセンス

Apache License, Version 2.0の下でライセンスされています。詳細についてはLICENSEファイルを参照してください。

## 貢献

貢献を歓迎します！貢献ガイドラインを読み、改善のためのプルリクエストを提出してください。

## サポート

問題や質問については、GitHubイシュートラッカーを使用してください。