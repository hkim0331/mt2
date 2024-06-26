# mt2

## Unreleased
* 受け取ったメッセージをデータベースへ保存する。
* 禁止ワード。
* 個人あてユニキャスト
* invalid anti-forgery-token を表示しないようにできるか？
* weekly に自動 save/reset
* 2022-01-19 errored in `lein ancient`
- [server-loop] WARN - unmasked client to server frame
- [BUG] フォントも黒にならないぞ。disabled=disabled が強いか。
- :duct.database.sql と :duct.server.http/http-kit {:port 3040} を
  config.edn で定義している。これは本当でなない。
- textarea を div で置き換えできないか？width, height が設定できるか？
  自動でボトムまでスクロールも面倒かな。
- abs already refers
```
Compiling mt2.handler.mt2
WARNING: abs already refers to: #'clojure.core/abs in namespace: taoensso.encore, being replaced by: #'taoensso.encore/abs
Compiling mt2.main
WARNING: abs already refers to: #'clojure.core/abs in namespace: medley.core, being replaced by: #'medley.core/abs
Compiling mt2.middleware
Compiling mt2.users
WARNING: abs already refers to: #'clojure.core/abs in namespace: medley.core, being replaced by: #'medley.core/abs
WARNING: abs already refers to: #'clojure.core/abs in namespace: taoensso.encore, being replaced by: #'taoensso.encore/abs
```
- びっくりモード: 自分の名前が表示される
- もっとビックリモード: 投稿者の名前が表示される
- バリデーションに引っ掛かったらダイアログを出す。
- 家から reload しようとしたらサーバーエラー。214 からは OK. 2023-10-07


## v1.5.341 / 2024-06-19
- changed the policy about version tag.
- on admin terminal, displays sender's login.

## 1.5.2 - 2023-04-16
- 1.5.1 を m24 からデプロイしたら動かない。
  nuc は vpn しないとダメだ。
  mt 以外も l22 で認証させるアプリは nuc では動作確認できない。
  認証をスキップするコードを忍ばせるか。

## 1.5.1 - 2023-04-14
- merged github

## 1.5.0 - 2023-04-14
- added hato 0.9.0
- replace mt2.users/find-user
- removed postgresql libraries

## v1.5.341 / 2024-06-19
## Added
- Makefile: uberjar, deplpy and clean

## 1.4.0 - 2022-06-09
### Fixme
- (defn- validate? [s])
  英語、日本語はバリデートできるが、絵文字ができない。

## 1.3.2 - 2022-04-14
- YOU は味気ない。🙋‍♀️ に変更。

## 1.3.1 - 2022-04-11
### Changed
- off disabled, color black.

## 1.3.0 - 2022-04-10
### Fixed
- 1.2.7 しか動かないと思っていたのは、Duct の環境変数の扱い方か。
  http-kit を使うので、それを config.edn に書かないといけないってのは自然だ。
- 「keydown でイベントが発生しない」それはブラウザが *.js をキャッシュしてたからじゃないか？


## 1.2.8 - 2022-04-10
- env.sh は必要なくなった。app.melt/mt2/start.sh に MT2_ADMIN を埋め込んでいる。
### Fixed
- DATABASE_URL, JDBC_DATABAE_URL を定義しても、反映されない。
  => config.edn に
    :duct.module/sql {}
  が抜けていた。これか？

## 1.2.7 - 2022-03-31
### Fixed
- [BUG] logout しても logout できてない。
- [BUG] flash を表示できない。
理由は redirect 先を flash を表示しようと待ち構えている `/login` じゃなく、
`/` に向けていたから。リダイレクト先からリダイレクトされることで、
flash の運命に従い、リクエストマップから消えていた。

## 1.2.6 - 2022-03-31
### Added
- admin でログインしたら reset ボタンを表示する。
### Changed
- textarea#output のフォントカラーを CSS で red に。
### Fixed
- [BUG] admin で reset できない。
  don't forget export MT2_ADMIN=admin
- [BUG] /reset で internal server error
  ログのセーブ先のフォルダ名が log と logs で違っていた。
  macOS では新たにフォルダを作成してエラーにならない。
  ubuntu だとエラーなのか？

### Changed
- production: systemd から shell script の呼び出しに変更。

## 1.2.5 - 2022-03-29
### Changed
- textarea#output のフォントカラーを CSS で black に。
### Added
- deploy.sh

## 1.2.4 - 2022-03-25

## 1.2.3 - 2022-03-24
- resume (aset output-el "scrollTop" (.-scrollHeight output-el))

## 1.2.2 - 2022-03-24
- auth against db
- add buddy-hashers postgresql dependencies
## 1.2.4 - 2022-03-25

## 1.2.1 - 2022-03-24
- index ページに
  [:input {:id "login" :type "hidden"  :name "login" :value "***")}]

## 1.1.1 - 2022-03-24
- ライブラリのバージョンアップ

## 1.1.0 - 2022-03-24
- メッセージの整理
- textarea#output disabled=disabled
- 自分のメッセージに --YOU-- のラベルをつける。reload でラベルが消える。

## 1.0.0 - 2022-03-23
restart for 2022. `1.0.0` does not mean `completed`.


## 0.10.1 BUG
* 動作しない。キャンセルせねば。チェックアウトした 0.9.6 は動く。

    % git checkout -b 0.9.6 refs/tags/0.9.6
## 0.10.0 - 2022-02-01
* lein ancient

## 0.9.6 - 2021-12-07
* reset したら msgs を`mt の新しい一週間の始まり` に初期化する。

## 0.9.5 - 2021-11-19
* 偽ビール対策。メッセージではなく、時刻に "🍶 " を入れた。

## 0.9.4 - 2021-10-08
* [hkim] やめて "🍺 " に変更。
* ログ整理 DEBUG -> INFO を試す。
* ビール文字に変更
* デバッグ、開発時にだけ読むコード。

## 0.9.3 - 2021-10-07
* admin でログインしたら、メッセージに [hkim] をプリペンド後、送信する。

## 0.8.10 - 2021-06-03
* 「ログインに失敗したら http」を login ページに。
* favicon.ico

## 0.8.9 - 2021-06-03
* nginx の https と Brave の組み合わせがバットかな。

## 0.8.8 - 2021-06-03
* メッセージを書き換えただけ。
  まだ Brave から mt.melt へセッションを開けない。

## 0.8.7 - 2021-06-03
* lein clean && lein uberjar してみる。brave から接続できた。
* clojure 1.10.3
* clojurescript 1.10.866

## (DEBUG) 0.8.6
* バグをレポートするバージョン。ログインできないのはセッションか？
* マックからログインできなくなった。バージョンチェックのため
  バージョンをページに明示する。on-site デバッグしないといけないか？

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
