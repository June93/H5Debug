//
//  AppDelegate.h
//  H5Debug
//
//  Created by WH-mac003 on 16/6/28.
//  Copyright © 2016年 WH-mac003. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "EMMessageBody.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

- (void)sendMessage:(EMMessageBody *)body;

- (void)logoutEM;

@end

