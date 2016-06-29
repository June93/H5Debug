//
//  ViewController.m
//  H5Debug
//
//  Created by WH-mac003 on 16/6/28.
//  Copyright © 2016年 WH-mac003. All rights reserved.
//

#import "ViewController.h"

#import "EMClient.h"
#import "EMMessageBody.h"
#import "EMTextMessageBody.h"
#import "EMConversation.h"

@interface ViewController ()<EMChatManagerDelegate, UIWebViewDelegate>

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view, typically from a nib.
    
    NSURL *url = [[NSURL alloc]initWithString:@"http://www.baidu.com"];
    [_webView_h5 loadRequest:[NSURLRequest requestWithURL:url]];
    
    //注册消息回调
    [[EMClient sharedClient].chatManager addDelegate:self delegateQueue:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)btn_sendMsg:(id)sender
{
    NSArray *arr_ems = [[EMClient sharedClient].chatManager getAllConversations];
    if (arr_ems.count == 0) {
        return;
    }
    
    EMConversation *ems = [arr_ems objectAtIndex:0];
    
    EMTextMessageBody *body = [[EMTextMessageBody alloc] initWithText:@"from iphone message"];
    NSString *from = [[EMClient sharedClient] currentUsername];
    
    EMMessage *message = [[EMMessage alloc] initWithConversationID:ems.conversationId from:from to:ems.conversationId body:body ext:nil];
    message.chatType = EMChatTypeChat; // 设置为单聊消息
    
    [[[EMClient sharedClient] chatManager] asyncSendMessage:message progress:^(int progress) {
        
        //
        
    } completion:^(EMMessage *message, EMError *error) {
        
        if (!error) {
            
            NSLog(@"消息发送成功");
        }
        
    }];
}

-(void)webViewDidFinishLoad:(UIWebView *)webView
{
    [self excuteJS:@"alert(\"test\")"];
}

- (void)excuteJS:(NSString *)js
{
    NSString *str = [_webView_h5 stringByEvaluatingJavaScriptFromString:js];
    
    NSLog(@"%@", str);
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
                NSLog(@"收到的文字是 txt -- %@",txt);
                
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
