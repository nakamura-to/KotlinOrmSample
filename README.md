# はじめに

このプロジェクトは [Server-Side Kotlin Study #1](https://server-sider-kotlin.connpass.com/event/210853/) で発表した「100%KotlinのORM・ktormを試してみた」のデモ用レポジトリです。

# 使い方

**セットアップ**

port番号がdocker-compose.yamlとソースコードにベタ書きなので適宜修正してください。

```bash
 # databaseコンテナ起動 
 $ docker-compose up
 # dabase初期化 & データ投入
 $ mysql -u root -p root -h 127.0.0.1 < sqls/00_initialize.sql
```

**動かし方**

あとはそれぞれ `Exposed.kt` と `Ktorm.kt` のmainメソッドを実行してください。