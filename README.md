# このプログラムについて
ターミナル上で動作するチャットアプリのサーバー&クライアントコード
シングルスレッドかつノンブロッキングで大量のクライアントをハンドリング
オブジェクトの再利用など

## 使用フレームワーク
なし

## 主要クラス
###### java-nioパッケージ内
* ByteBuffer
* SocketChannel
* Selector

###### 自作クラス
* "Server|Client"Manager(いわゆるメインクラス)
* Reader(受信)
* Processor(受信データの処理)
* Writer(送信)
* InputExchange(入力データの実体)
* OutputExchange(出力データの実体)
* InputExchanges(入力データ管理)
* OutputExchanges(出力データの管理)

###### 注意事項
すべての機能が実装されているわけではありません
チャットアプリとして機能する土台は８割ほど完成していると認識
(クライアントの切断周りで手を加える必要があります)
