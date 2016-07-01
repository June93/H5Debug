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
    
    _lblMsg.hidden = YES;
    _btnSend.hidden = YES;
    
    _view_debug.hidden = YES;
    _lbl_debug.hidden = YES;
    
    _btn_reload.layer.cornerRadius = _btn_reload.frame.size.width/2.0;
    _btn_reload.layer.masksToBounds = YES;
    [_btn_reload setBackgroundImage:[Utility imageWithColor:[Utility colorFromHexRGB:@"66cc00"]] forState:UIControlStateNormal];
    [_btn_reload setBackgroundImage:[Utility imageWithColor:[Utility colorFromHexRGB:@"4caf50"]] forState:UIControlStateHighlighted];
    
    [_lbl_debug.layer addAnimation:[self opacityForever_Animation:1] forKey:nil];
    
    UITapGestureRecognizer *tapGr = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(exit_debug)];
    [_view_debug addGestureRecognizer:tapGr];
    
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
    
    EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:@"from iphone message"];
    [appd sendMessage:body];
}

- (IBAction)btn_reload_click:(id)sender
{
    [_webView_h5 reload];
}

#pragma mark - Selector
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
        return;
    }
    
    NSArray *arr_components = [text componentsSeparatedByString:@":"];
    
    if (arr_components.count == 2) {
        
        NSString *key = [arr_components objectAtIndex:0];
        
        NSString *value = [arr_components objectAtIndex:1];
        
        if ([key isEqualToString:@"javascript"]) {
            
            [self excuteJS:value];
            
        } else if ([key isEqualToString:@"control"]) {
            
            if ([value isEqualToString:@"screen shot"]) {
                
                EMImageMessageBody *body = [[EMImageMessageBody alloc] initWithData:[self screenShot] displayName:@"screenShot.jpg"];
                
                AppDelegate *appd = (AppDelegate *)[[UIApplication sharedApplication] delegate];
                [appd sendMessage:body];
            }
            
        } else {
            
            
        }
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
                
                [self parseMessage:txt];
            }
                
                break;
                
            default:
                break;
        }
    }
}

@end
