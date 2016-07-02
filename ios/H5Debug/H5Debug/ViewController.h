//
//  ViewController.h
//  H5Debug
//
//  Created by WH-mac003 on 16/6/28.
//  Copyright © 2016年 WH-mac003. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ViewController : UIViewController

@property (nonatomic, weak) IBOutlet UIWebView *webView_h5;

@property (nonatomic, weak) IBOutlet UILabel *lblMsg;

@property (nonatomic, weak) IBOutlet UIButton *btnSend;

@property (nonatomic, weak) IBOutlet UIView *view_debug;
@property (nonatomic, weak) IBOutlet UILabel *lbl_debug;

@property (nonatomic, weak) IBOutlet UIButton *btn_reload;

- (void)loadH5:(NSString *)address;

- (void)loadTargetH5;

@end

