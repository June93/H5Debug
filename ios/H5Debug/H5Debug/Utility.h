//
//  Utility.h
//  H5Debug
//
//  Created by pjboss on 16/7/1.
//  Copyright © 2016年 WH-mac003. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <UIKit/UIKit.h>

@interface Utility : NSObject

+ (NSString *)makeUrl:(NSString *)openUrl;

+ (UIImage *)imageWithColor:(UIColor *)color;

+ (UIColor *)colorFromHexRGB:(NSString *)inColorString;

@end
