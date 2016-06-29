//
//  WebConsole.m
//  H5Debug
//
//  Created by WH-mac003 on 16/6/29.
//  Copyright © 2016年 WH-mac003. All rights reserved.
//

#import "WebConsole.h"

@implementation WebConsole

+ (void) enable
{
    [NSURLProtocol registerClass:[WebConsole class]];
}

+ (BOOL)canInitWithRequest:(NSURLRequest *)request
{
    //http://debugger/console-log
    
    if ([[[request URL] host] isEqualToString:@"debugger"]) {
        
        NSString *webview_log = [[[request URL] path] substringFromIndex:1];
        
        NSLog(@"%@", webview_log);
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"console_log" object:webview_log];
    }
    
    return FALSE;
}

@end
