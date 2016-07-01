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
#import "Utility.h"
#import "constant.h"
#import "NSString+URLEncoding.h"

#import "NJKWebViewProgress.h"
#import "NJKWebViewProgressView.h"

@interface ViewController ()<EMChatManagerDelegate, UIWebViewDelegate, NJKWebViewProgressDelegate>
{
    NJKWebViewProgressView *_progressView;
    NJKWebViewProgress *_progressProxy;
    
    UIButton *backBtn;
}

@end

@implementation ViewController

-(void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    backBtn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 60, 44)];
    [backBtn setExclusiveTouch:YES];
    [backBtn addTarget:self action:@selector(back_click) forControlEvents:UIControlEventTouchUpInside];
    [backBtn setImage:[UIImage imageNamed:@"btn_back"] forState:UIControlStateNormal];
    [backBtn setTitleColor:[Utility colorFromHexRGB:@"249bff"] forState:UIControlStateNormal];
    [backBtn setTitle:@"返回" forState:UIControlStateNormal];
    backBtn.imageEdgeInsets = UIEdgeInsetsMake(0, -30, 0, 0);
    backBtn.titleEdgeInsets = UIEdgeInsetsMake(0, -22, 0, 0);
    backBtn.titleLabel.font = [UIFont systemFontOfSize:16.0];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:backBtn];
    backBtn.hidden = YES;
    
    _lblMsg.hidden = YES;
    _btnSend.hidden = YES;
    
    AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    if (!appd.isDebug) {
        
        _view_debug.hidden = YES;
        _lbl_debug.hidden = YES;
        [self loadH5:default_address];
        
    } else {
        
        _view_debug.hidden = NO;
        _lbl_debug.hidden = NO;
        
        if (appd.targetUrl && appd.targetUrl.length > 0) {
            
            appd.targetUrl = [appd.targetUrl URLDecodedString];
            
            [self loadH5:appd.targetUrl];
            
        } else {
            
            [self loadH5:default_address];
        }
    }
    
    _btn_reload.layer.cornerRadius = _btn_reload.frame.size.width/2.0;
    _btn_reload.layer.masksToBounds = YES;
    [_btn_reload setBackgroundImage:[Utility imageWithColor:[Utility colorFromHexRGB:@"66cc00"]] forState:UIControlStateNormal];
    [_btn_reload setBackgroundImage:[Utility imageWithColor:[Utility colorFromHexRGB:@"4caf50"]] forState:UIControlStateHighlighted];
    
    [_lbl_debug.layer addAnimation:[self opacityForever_Animation:1] forKey:nil];
    
    UITapGestureRecognizer *tapGr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(exit_debug)];
    [_view_debug addGestureRecognizer:tapGr];
    
    _progressProxy = [[NJKWebViewProgress alloc] init];
    _webView_h5.delegate = _progressProxy;
    _progressProxy.webViewProxyDelegate = self;
    _progressProxy.progressDelegate = self;
    
    CGFloat progressBarHeight = 2.f;
    CGRect navigationBarBounds = self.navigationController.navigationBar.bounds;
    CGRect barFrame = CGRectMake(0, navigationBarBounds.size.height - progressBarHeight, navigationBarBounds.size.width, progressBarHeight);
    _progressView = [[NJKWebViewProgressView alloc] initWithFrame:barFrame];
    _progressView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleTopMargin;
    
    [WebConsole enable];
    
    //监听日志输出
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(send_console_log:) name:@"console_log" object:nil];
    
    //注册消息回调
    [[EMClient sharedClient].chatManager addDelegate:self delegateQueue:nil];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [_progressView setProgress:0 animated:YES];
    [self.navigationController.navigationBar addSubview:_progressView];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    [_progressView removeFromSuperview];
}

- (IBAction)btn_sendMsg:(id)sender
{
    AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:@"from iphone message"];
    [appd sendMessage:body];
}

- (IBAction)btn_reload_click:(id)sender
{
    [_webView_h5 reload];
}

#pragma mark - Selector
- (void)back_click
{
    [self.webView_h5 goBack];
}

//加载指定页面
- (void)loadH5:(NSString *)address
{
    NSURL *url = [[NSURL alloc] initWithString:[Utility makeUrl:address]];
    [_webView_h5 loadRequest:[NSURLRequest requestWithURL:url]];
}

- (void)exit_debug
{
    _view_debug.hidden = YES;
    _lbl_debug.hidden = YES;
    
    AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    
    appd.isDebug = NO;
    
    EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:@"退出调试模式"];
    [appd sendMessage:body];
    
    //退出环信
    [appd logoutEM];
}

- (void)send_console_log:(NSNotification *)notify
{
    NSString *log = (NSString *)[notify object];
    
    EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:log];
    
    AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    [appd sendMessage:body];
}

- (void)excuteJS:(NSString *)js
{
    NSString *js_handle = [NSString stringWithFormat:@"setTimeout(function(){%@;}, 1);", js];
    
    NSString *str = [_webView_h5 stringByEvaluatingJavaScriptFromString:js_handle];
    
    NSLog(@"%@", str);
}

- (NSData *)screenShot
{
    self.view.backgroundColor = [UIColor greenColor];
    
    UIWindow *screenWindow = [[UIApplication sharedApplication] keyWindow];
    
    UIGraphicsBeginImageContext(screenWindow.frame.size);
    
    [screenWindow.layer renderInContext:UIGraphicsGetCurrentContext()];
    
    UIImage * viewImage = UIGraphicsGetImageFromCurrentImageContext();
    
    UIGraphicsEndImageContext();
    
    return UIImageJPEGRepresentation(viewImage, 0.8);
}

- (void)parseMessage:(NSString *)text
{
    if (text.length == 0) {
        
        EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:@"无效指令"];
        
        AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
        [appd sendMessage:body];
        
        return;
    }
    
    if ([text hasPrefix:@"javascript:"]) {
        
//        NSString *value = [text substringFromIndex:11];
       NSString *value = [text stringByReplacingOccurrencesOfString:@"javascript:" withString:@""];
        
        [self excuteJS:value];
        
    } else if ([text hasPrefix:@"control:"]) {
        
//        NSString *value = [text substringFromIndex:8];
        NSString *value = [text stringByReplacingOccurrencesOfString:@"control:" withString:@""];
        
        if ([value isEqualToString:@"screen shot"]) {
            
            EMImageMessageBody *body = [[EMImageMessageBody alloc] initWithData:[self screenShot] displayName:@"screenShot.jpg"];
            
            AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
            [appd sendMessage:body];
            
        } else {
            
            EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:@"无效指令"];
            
            AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
            [appd sendMessage:body];
        }
        
    } else {
        
        EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:@"无效指令"];
        
        AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
        [appd sendMessage:body];
    }
    
}

-(CABasicAnimation *)opacityForever_Animation:(float)time
{
    CABasicAnimation *animation = [CABasicAnimation animationWithKeyPath:@"opacity"];
    animation.fromValue = [NSNumber numberWithFloat:1.0f];
    animation.toValue = [NSNumber numberWithFloat:0.0f];//这是透明度。
    animation.autoreverses = YES;
    animation.duration = time;
    animation.repeatCount = MAXFLOAT;
    animation.removedOnCompletion = NO;
    animation.fillMode = kCAFillModeForwards;
    animation.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseIn];
    
    return animation;
}

#pragma mark - UIWebViewDelegate
-(void)webViewDidFinishLoad:(UIWebView *)webView
{
    backBtn.hidden = !_webView_h5.canGoBack;
}

#pragma mark - NJKWebViewProgressDelegate
-(void)webViewProgress:(NJKWebViewProgress *)webViewProgress updateProgress:(float)progress
{
    [_progressView setProgress:progress animated:YES];
    
    self.navigationItem.title = [_webView_h5 stringByEvaluatingJavaScriptFromString:@"document.title"];
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
                
                [self parseMessage:txt];
            }
                
                break;
                
            default:
                break;
        }
    }
}

@end
