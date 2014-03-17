# Web site visit statictics

This app can record page visit,get visit count for page,for category and for site.

## url参数约定如下：

* siteid，必须参数，如果没有这个参数，服务器不做任何动作，直接返回。必须先在http://sites.fh.gov.cn/vrapp/index.html注册站点
* record=true，就会记录此次访问的referer
* out=wholesite || thispage
* 如果有domid参数，就会输出一段js代码，用访问次数替换dom里面的内容。
* 如果有callback参数，输出的代码回调用callback指定的函数，传入数值。
* 如果domid和callback都有，callback优先。

## 看几个常用的场景：

1、需要在每个页面的尾部，显示整个网站的访问次数。

```
<script src="http://vr.fh.gov.cn?siteid=some-uuid&record=true&out=wholesite&domid=site-counter"></script>
```

2、在文章页面显示访问次数，因为已经在公共尾部参与了记录，文章页的record参数不要再加上，否则会造成重复计数。

```
<script src="http://vr.fh.gov.cn?siteid=some-uuid&out=thispage&domid=site-counter"></script>
```

用来统计站点的文章访问数，同时记录详细的客户端信息，包括ip，ua。引入的数据可以在后台通过mongodb的MapReduce或者hadoop分析处理。
