//
//  ViewController.m
//  H5Debug
//
//  Created by WH-mac003 on 16/6/28.
//  Copyright © 2016年 WH-mac003. All rights reserved.
//

#import "ViewController.h"

#import "AppDelegate.h"
#import "EMClient.h"
#import "EMMessageBody.h"
#import "WebConsole.h"
#import "PGToast.h"

#define default_address @"http://10.5.103.69:8081/h5debug/phone/index.html"

@interface ViewController ()<EMChatManagerDelegate, UIWebViewDelegate>

@end

@implementation ViewController

-(void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    [WebConsole enable];
    
    [self loadH5:default_address];
    
    //监听日志输出
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(send_console_log:) name:@"console_log" object:nil];
    
    //注册消息回调
    [[EMClient sharedClient].chatManager addDelegate:self delegateQueue:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)btn_sendMsg:(id)sender
{
    AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    [appd sendMessage:@"from iphone message"];
}

#pragma mark - Selector
//加载指定页面
- (void)loadH5:(NSString *)address
{
    NSURL *url = [[NSURL alloc]initWithString:address];
    [_webView_h5 loadRequest:[NSURLRequest requestWithURL:url]];
}

- (void)send_console_log:(NSNotification *)notify
{
    NSString *log = (NSString *)[notify object];
    
    AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    [appd sendMessage:log];
}

- (void)excuteJS:(NSString *)js
{
    NSString *js_handle = [NSString stringWithFormat:@"setTimeout(function(){%@;}, 1);", js];
    
    NSString *str = [_webView_h5 stringByEvaluatingJavaScriptFromString:js_handle];
    
    NSLog(@"%@", str);
}

#pragma mark - UIWebViewDelegate
-(void)webViewDidFinishLoad:(UIWebView *)webView
{
    //
}

#pragma mark - EMChatManagerDelegate
-(void)didReceiveMessages:(NSArray *)aMessages
{
    for (EMMessage *message in aMessages) {
        
        EMMessageBody *msgBody = message.body;
        
        switch (msgBody.type) {
                
            case EMMessageBodyTypeText:
            {
                //收到的文字消息
                EMTextMessageBody *textBody = (EMTextMessageBody *)msgBody;
                NSString *txt = textBody.text;
                NSLog(@"收到的文字是 -- %@",txt);
                
                _lblMsg.text = txt;
                
                [self excuteJS:txt];
            }
                
                break;
                
            default:
                break;
        }
    }
}

@end
