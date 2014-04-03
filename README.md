# Web site visit statictics

This app can record page visit,get visit count for page,for category and for site.

## 注册问题

* 你不需要注册，将代码加入你的网页即可工作。
* 注册之后，才能管理和查看站点的统计，还可以将站点组成群，统计群内的访问排行等。

## url参数约定如下：

* out=wholesite || thispage
* 如果有domid参数，就会输出一段js代码，用访问次数替换dom里面的内容。
* 如果有callback参数，输出的代码回调用callback指定的函数，传入数值。
* 如果domid和callback都有，callback优先。
* 如果有silent=true，那么返回空白字符串。


## 注意，只有out=wholesite的请求会将referer保存到数据库中，所以必须确保每个页面都存在这个请求。其它类型的out不会再次将referer保存到数据库中去。也就是说，同一张页面中，只要保证out=wholesite出现一次，其它类型可以多次出现。

## 最简单的版本，同步版本。（不推荐）

服务器返回类型：
```
document.write(33); //33是计数值。
```

1、需要在需要显示的位置，整个网站的访问次数。

```
<script src="http://vr.m3958.com?out=wholesite"></script>
```

2、在文章页面显示访问次数.

```
<script src="http://vr.m3958.com?out=thispage"></script>
```
## 异步版本

服务器返回类型：
```
(function(){
	var dom = document.getElementById("you-domid-parameters");
	dom.innerHTML = "33";
})();
```

1、需要在每个页面的尾部，显示整个网站的访问次数。

```
<script src="http://vr.m3958.com?out=wholesite&domid=site-stats"></script>
```

2、在文章页面显示访问次数

```
<script src="http://vr.m3958.com?out=thispage&domid=page-stat"></script>
```

## 异步的callback版本，随心所有的控制。

你需要在页面上先定义回调函数，让后将函数名作为callback参数。

```
function cb(num){
	//split number,and use gif or other technology to display what ever you like.
};
```

服务器返回类型：
```
cb(33);
```

1、需要在每个页面的尾部，显示整个网站的访问次数。

```
<script src="http://vr.m3958.com?out=wholesite&callback=cb"></script>
```
2、在文章页面显示访问次数

```
<script src="http://vr.m3958.com?out=thispage&callback=cb"></script>
```
用来统计站点的文章访问数，同时记录详细的客户端信息，包括ip，ua。引入的数据可以在后台通过mongodb的MapReduce或者hadoop分析处理。
