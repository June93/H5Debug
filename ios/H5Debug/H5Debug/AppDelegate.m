//
//  AppDelegate.m
//  H5Debug
//
//  Created by WH-mac003 on 16/6/28.
//  Copyright © 2016年 WH-mac003. All rights reserved.
//

#import "AppDelegate.h"

#import "EMSDK.h"

#import "ViewController.h"

#import "PGToast.h"

#import "NSString+URLEncoding.h"

@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    
    EMOptions *options = [EMOptions optionsWithAppkey:@"acetrump#h5-debug"];
    
    [[EMClient sharedClient] initializeSDKWithOptions:options];
    
    EMError *error = [[EMClient sharedClient] loginWithUsername:@"iphone" password:@"123456"];
    if (!error) {
        
        NSLog(@"登录成功");
        
        [PGToast makeToast:@"连接成功"];
        
        [[EMClient sharedClient].options setIsAutoLogin:YES];
        
    } else {
        
        NSLog(@"%@", error.errorDescription);
    }
    
    return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    
    // APP进入后台
    [[EMClient sharedClient] applicationDidEnterBackground:application];
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    
    // APP进入前台
    [[EMClient sharedClient] applicationWillEnterForeground:application];
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

-(BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<NSString *,id> *)options
{
    BOOL isExistTargetUrl = NO;
    
    NSString *targetUrl = @"";
    
    NSString *str_url = [url absoluteString];
    
    NSLog(@"%@", str_url);
    
    NSArray *arr_question = [str_url componentsSeparatedByString:@"?"];
    
    if (arr_question.count >= 2) {
        
        NSString *str_param = [arr_question objectAtIndex:1];
        
        NSArray *arr_param = [str_param componentsSeparatedByString:@"&"];
        
        for (NSString *param in arr_param) {
            
            NSArray *arr_value = [param componentsSeparatedByString:@"="];
            
            if (arr_value.count == 2) {
                
                NSString *key = [arr_value objectAtIndex:0];
                NSString *value = [arr_value objectAtIndex:1];
                
                NSLog(@"key = %@ value = %@", key, value);
                
                if ([key isEqualToString:@"targetUrl"]) {
                    
                    isExistTargetUrl = YES;
                    
                    targetUrl = value;
                    
                    break;
                }
            }
        }
    }
    
    //刷新 
    ViewController *vc = (ViewController *)self.window.rootViewController;
    
    if (isExistTargetUrl) {
        
        targetUrl = [targetUrl URLDecodedString];
        
        [vc loadH5:targetUrl];
        
    } else {
        
        [vc.webView_h5 reload];
    }
    
    return YES;
}

@end
