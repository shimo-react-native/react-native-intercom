//
//  IntercomEventEmitter.m
//  RNIntercom
//
//  Created by Roger Chapman on 4/11/16.
//  Copyright © 2016 Jason Brown. All rights reserved.
//

#import "IntercomEventEmitter.h"
#import <Intercom/Intercom.h>

@implementation IntercomEventEmitter

RCT_EXPORT_MODULE();

- (NSDictionary<NSString *, NSString *> *)constantsToExport {
    return @{
             @"UNREAD_CHANGE_NOTIFICATION": IntercomUnreadConversationCountDidChangeNotification,
             @"WINDOW_WILL_SHOW_NOTIFICATION": IntercomWindowWillShowNotification,
             @"WINDOW_DID_SHOW_NOTIFICATION": IntercomWindowDidShowNotification,
             @"WINDOW_WILL_HIDE_NOTIFICATION": IntercomWindowWillHideNotification,
             @"WINDOW_DID_HIDE_NOTIFICATION": IntercomWindowDidHideNotification,
             @"DID_START_NEW_CONVERSATION_NOTIFICATION": IntercomDidStartNewConversationNotification
             };
}

- (NSArray<NSString *> *)supportedEvents {
    return @[
             IntercomUnreadConversationCountDidChangeNotification,
             IntercomWindowWillShowNotification,
             IntercomWindowDidShowNotification,
             IntercomWindowDidShowNotification,
             IntercomWindowWillHideNotification,
             IntercomWindowDidHideNotification,
             IntercomDidStartNewConversationNotification
             ];
}

- (void)startObserving {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleUpdateUnreadCount:) name:IntercomUnreadConversationCountDidChangeNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleWindowWillShow:) name:IntercomWindowWillShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleWindowDidShow:) name:IntercomWindowDidShowNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleWindowWillHide:) name:IntercomWindowWillHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleWindowDidHide:) name:IntercomWindowDidHideNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleDidStartNewConversation:) name:IntercomDidStartNewConversationNotification object:nil];
}

- (void)stopObserving {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

#pragma mark - Notification

- (void)handleUpdateUnreadCount:(NSNotification *)notification {
    __weak IntercomEventEmitter *weakSelf = self;
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.1 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        IntercomEventEmitter *strongSelf = weakSelf;
        NSUInteger unreadCount = [Intercom unreadConversationCount];
        NSNumber *unreadCountNumber = [NSNumber numberWithUnsignedInteger: unreadCount];
        NSDictionary *body = @{@"count": unreadCountNumber};
        [strongSelf sendEventWithName:IntercomUnreadConversationCountDidChangeNotification body:body];
    });
}

- (void)handleWindowWillShow:(NSNotification *)notification {
    [self sendEventWithName:IntercomWindowWillShowNotification body:nil];
}

- (void)handleWindowDidShow:(NSNotification *)notification {
    [self sendEventWithName:IntercomWindowDidShowNotification body:nil];
}

- (void)handleWindowWillHide:(NSNotification *)notification {
    [self sendEventWithName:IntercomWindowWillHideNotification body:nil];
}

- (void)handleWindowDidHide:(NSNotification *)notification {
    [self sendEventWithName:IntercomWindowDidHideNotification body:nil];
}

- (void)handleDidStartNewConversation:(NSNotification *)notification {
    [self sendEventWithName:IntercomDidStartNewConversationNotification body:nil];
}

@end
