# navigation-android

[![Download](https://api.bintray.com/packages/interactiveservices/maven/navigation-android/images/download.svg)](https://bintray.com/interactiveservices/maven/navigation-android/_latestVersion)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://opensource.org/licenses/MIT)
[![API](https://img.shields.io/badge/API-14%2B-green.svg)](https://developer.android.com/about/versions/android-4.0.html)

Простой выбор программы для построения маршрута  
можно использовать Yandex и Google   

![sample](https://raw.githubusercontent.com/interactiveservices/navigation-android/master/screens/photo1.png)
![alert](https://raw.githubusercontent.com/interactiveservices/navigation-android/master/screens/alert.png)

Использование

1) Инициализация библиотеки
```java
// with builder
new NavigatorHelper.Builder(this).setUseYandexMap(false)
                .setRouteType(true)
                .setSaveCommand(true)
                .init();
```
или

```java
// or use default settings
NavigatorHelper.init(this);
```
2) Пример вызова:

```java
// show bottomSheetDialog  
NavigatorHelper.showChooseNavigationDialog(getSupportFragmentManager(),
                                                           MainActivity.this,
                                                           55.76009f,
                                                           37.648801f);
```                                                           

```java
// show alertDialog
NavigatorHelper.showChooseNavigationAlertDialog(getSupportFragmentManager(),
                                                           MainActivity.this,
                                                           55.76009f,
                                                           37.648801f);
```                   

3) Кастомизация  
  
| Параметр | Описание |
| ------------ | ------------------------------------------------------------------ |
| setDebug | включить режим отладки и вывод в консоль с тегом "NavigatorHelper" 
| useYandexNav | использовать Яндекс Навигатор |
| useYandexMap | использовать Яндекс Карты |
| useGoogleMaps | использовать Google Maps |
| saveCommand | сохранять выбор пользователя |
| title | Заголовок в диалогах |
| saveTitle | Заголовок для сохранения команды |
  
