//
//  AppDelegate.m
//  H5Debug
//
//  Created by WH-mac003 on 16/6/28.
//  Copyright © 2016年 WH-mac003. All rights reserved.
//

#import "AppDelegate.h"

#import "EMSDK.h"

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
    NSString *str_url = [url absoluteString];
    
    NSLog(@"%@", str_url);
    
    return YES;
}

@end
