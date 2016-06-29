相关网页地址：
1、WebIM地址 http://10.5.103.69:8081/h5debug/index.html
2、启动APP二维码地址：
   2.1、http://10.5.103.69:8081/h5debug/h5debug.html 
     不会给APP传参数
   2.2、http://10.5.103.69:8081/h5debug/h5debug.html?targetUrl=http%3A%2F%2F10.5.103.69%3A8081%2Fh5debug%2Fphone%2Fdev.html
     启动APP时会给APP传递 "targetUrl" 参数，客户端取到参数后，需要打开指定的页面，注意参数需要做URL解码
3、APP 默认加载网页 http://10.5.103.69:8081/h5debug/phone/index.html


日志输出原理
1、iOS 通过模拟请求，Android通过console.log标准输出，代码如下

function writeLog(message){
  if(browser.versions.ios){
     var xhr = new XMLHttpRequest();
     xhr.open('GET', "http://h5debug/" +
      encodeURIComponent(message));
     xhr.send(null);
  }else{
     console.log(message);
  }
}
