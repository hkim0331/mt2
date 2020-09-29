# mt2

## Unreleased
* ClojureScript からログを書き出す。
* clj-kondo が deps.edn の mt2 に赤マークをつけるのはなぜ？
  unresolved symbol mt2
* 受け取ったメッセーじをデータベースへ保存する。
* 禁止ワード。
* エンターで送信。
* `state changed` と nil が表示されてしまう。

```
Tue Sep 29 2020 15:46:40
  nil
Handshake:OK
READY!
state changed: {:type :ws, :open? true, :ever-opened? true,
:csrf-token "tWyrunQ8I2vNm8NN6qdGoD9sxE74JHt3KG31FWeget+BLw4C30Os
Q5b7vo/3uWqSxLrEMmU9eSqHFq/2", :uid :taoensso.sente/nil-uid, :handshake-data nil, :first-open? false, :last-ws-close
{:udt1601361975595, :ev #object[CloseEvent [object CloseEvent]],
:clean? true, :code 1001, :reason ""}}
```

## 0.4.0 - 2020-09-29
* nginx r-proxy
* systemd
* httpbasic 認証


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
