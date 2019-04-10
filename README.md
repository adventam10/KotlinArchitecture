# KotlinArchitecture

[これ](https://github.com/adventam10/TestApplicationArchitecture)のAndroid(Kotlin)版
Androidアプリのアーキテクチャの考察のためのリポジトリです。（私はMVCもよくわからない...）

予定ではブランチごとにMVCで同じアプリを作成する予定です。(masterは何も考えずに作ったやつ)

作るアプリは下の要件のやつ

## アプリ要件

[livedoor天気のWeb API](http://weather.livedoor.com/weather_hacks/webservice)(商用利用不可)を利用した各都道府県の天気を表示するアプリ。

対象はAndroid 5.0以降。

### 機能
４７都道府県を一覧表示し、選択した都道府県の今日、明日、明後日の３日間の天気を表示する
* 都道府県一覧の表示は各地方で絞り込みができる
* 都道府県のお気に入り登録ができる（端末で記憶する）
* 都道府県一覧の表示はお気に入りで絞り込みができる

### 画面構成
都道府県一覧画面と詳細画面の２画面。
#### 都道府県一覧画面
![default](https://user-images.githubusercontent.com/34936885/41820589-06b826d6-780f-11e8-841c-42a95b27b6a7.png)

* ４７都道府県を一覧表示
* 都道府県選択で通信で天気情報を取得し詳細画面に遷移する（エラー時は遷移しない）
* 星マークタップでお気に入り登録削除を行う
* お気に入りのみ表示チェックボックスのチェックでお気に入りのみ表示する
* 地方で絞り込みボタンで下の画像のようなポップを表示する

![default](https://user-images.githubusercontent.com/34936885/41820592-1559e7c4-780f-11e8-9409-bcab0515e897.png)

* チェックの切り替えで一覧表示を絞り込んで表示する
#### 詳細画面
![default](https://user-images.githubusercontent.com/34936885/41820596-22f70d80-780f-11e8-868f-5649566376dc.png)

* 選択した都道府県名を表示する
* 選択した都道府県の今日、明日、明後日の３日間の天気を表示する
* 表示内容は日付、アイコン、天気、最高気温、最低気温の５項目を表示する
* 右上の再読み込みボタンで天気情報を通信で再取得する

## コメント
そんな感じのアプリを色々な構成で作ってみようと思います。
