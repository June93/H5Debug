//
//  Utility.m
//  H5Debug
//
//  Created by pjboss on 16/7/1.
//  Copyright © 2016年 WH-mac003. All rights reserved.
//

#import "Utility.h"

@implementation Utility

+ (NSString *)makeUrl:(NSString *)openUrl
{
    if (![openUrl isKindOfClass:[NSString class]] || openUrl.length == 0) {
        return @"";
    }
    
    if (![openUrl hasPrefix:@"http://"] && ![openUrl hasPrefix:@"https://"]) {
        
        openUrl = [NSString stringWithFormat:@"http://%@", openUrl];
    }
    
    NSString *timestamp = [NSString stringWithFormat:@"%.0f", [[NSDate date] timeIntervalSince1970]*1000];
    
    NSUInteger loc = [openUrl rangeOfString:@"?"].location;
    if(loc == NSNotFound)
    {
        //无参数
        return [NSString stringWithFormat:@"%@?t=%@", openUrl, timestamp];
    }
    else
    {
        // 有参数
        NSString *domain,*args;
        domain = [openUrl substringToIndex:loc+1];
        args = [openUrl substringFromIndex:loc+1];
        
        if(args.length)
        {
            NSMutableArray *ar = [NSMutableArray arrayWithArray:[args componentsSeparatedByString:@"&"]];
            for (NSString *arg in ar)
            {
                domain = [domain stringByAppendingFormat:@"%@&",arg];
            }
        }
        
//        NSString *result = [domain stringByAppendingString:@"__from=wzs"];
        return [NSString stringWithFormat:@"%@&t=%@", domain, timestamp];
    }
}


@end
