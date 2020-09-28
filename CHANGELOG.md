# mt2

## Unreleased
* ClojureScript からログを書き出す。
* clj-kondo が deps.edn の mt2 に赤マークをつけるのはなぜ？
  unresolved symbol mt2
* 受け取ったメッセーじをデータベースへ保存する。
* 禁止ワード。
* エンターで送信。


## 0.3.0 - 2020-09-28
* 長すぎるメッセージを受け取らない。0 &lt; len &lt; MAX_MSG_LEN=70
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
