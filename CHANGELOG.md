# mt2

## Unreleased
* 受け取ったメッセージをデータベースへ保存する。
* 禁止ワード。
* 個人あてユニキャスト
* return で送信できるように(しないほうがいいかも)
* send ボタンが iPhone ではみ出る。
* login/password/submit を１行におさめる。

## 0.8.4 - 2021-5-28
* test/mt2: clj-kondo が warning しないよう、:all を書き換え。
* bugfix bump-version.sh: version-string を書き換えないように。
* textarea 高さ調整。iPhone8 で全画面が出るように。
* version-string を textarea:placeholder で表示。
* handshake 時にメッセージを出さない。timbre/debug で。

## 0.8.2 - 2021-05-26
* メッセージを正順に表示。
* input と textarea の上下を反転。
* bump-version.sh: ${HOMEBREW_PREFIX}
* reload で逆順
* メッセージを受信したらボトムまでスクロール。

## 0.8.0 - 2020-11-14
* メッセージ受信したら ping 鳴らす。

## 0.7.2 - 2020-10-21
* mt2/page から [::response/ok] 等を外に出す。
  認証失敗な時とかに、[::response/unauthorized]を返したかったから。
  これでいいのかな？


## 0.7.1 - 2020-10-21
* reset に save の機能を含める。


## 0.7.0 - 2020-10-07
* 認証を http-basic から session に変更。
* admin でログインした時のみ、/reset、/save できる。
* code polish up.

## 0.6.2 - 2020-10-07
* reset メッセージをクリアする。
* save メッセージを logs/ 以下に現在時間でセーブする。

## 0.6.1 - 2020-10-05
* lein ancient
  all artifacts are up-to-date.
* cljstyle fix src


## 0.6.0 - 2020-10-05
* メッセージに日付をプリペンドする仕事をサーバに寄せる。
* サーバにためたメッセージを reload で取り出す。
* resume ボタンを外した。


## 0.5.0 - 2020-10-03
* resume ボタン。
  クライアントに届いたメッセージは clear で一旦非表示にできる。



## 0.4.3 - 2020-10-01
* 日付の代わりにバージョン番号


## 0.4.2 - 2020-09-30
* FIXED: environ で渡ってくるのは lisp-case に変換されたキーワード。
* FIXED: [:chsk/ping] は [:chsk/ws-ping] が正しい。


## 0.4.1 - 2020-09-30
* アカウント・パスワードを環境変数から。
* systemd/stop.sh で mt2 を止める。
* [:chsk/ping] を表示しない。nil の表示を止める。


## 0.4.0 - 2020-09-29
* httpbasic 認証
* Opera のキャッシュが邪魔をする。
* nginx r-proxy
* systemd


## 0.3.1 - 2020-09-28
* clj-kondo, cljstyle でリファクタ。


## 0.3.0 - 2020-09-28
* 長すぎるメッセージを受け取らない。0 &lt; len &lt; MAXi\_MSGi\_LEN=70
* 常にテキストエリアの先頭を表示する。

```clj
(aset output-el "scrollTop" 0) ; 0 を (.-scrollHeight obj))で最下行
```
* 不要なテキスト（デバッグ用テキスト）をテキストエリアに出さない。
* カラのメッセージは受け取らない。
* bootstrap
* JavaScript での日付取得は (sub (str (js/Date.) 0 25) で。


## 0.2.0 - 2020-09-28
* uberjar 作って動作確認。
* start to work as a micro twitter.
* websocket ブロードキャスト
* message, output のサイズ調整


## 0.1.2 - 2020-09-28
* http-kit


## 0.1.1 - 2020-09-28
* ADD clear button.
* ADD CHANGELOG.md file.
* check taoensso/timbre usage.


## 0.1.0 - 2020-09-28
* +api can not handle /js/main.js as I expect. +site does.
* ADD bump-versio.sh script.
