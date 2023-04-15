//
//  RCTSmartReplyModule.m
//  smartreply
//
//  Created by Łukasz Kurant on 14/04/2023.
//

#import "RCTSmartReplyModule.h"
#import <MLKit.h>

@implementation RCTSmartReplyModule

RCT_EXPORT_MODULE(SmartReplyModule);


RCT_EXPORT_METHOD(getSmartReply:(NSArray *)messages
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{

  
  dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{

    NSMutableArray *conversation = [NSMutableArray array];
    
    for(int i = 0; i < messages.count; i++) {
      NSDictionary* messageObject = [messages objectAtIndex:i];
      
      MLKTextMessage *message = [[MLKTextMessage alloc]
          initWithText:  [messageObject objectForKey:@"text"]
          timestamp: [NSDate date].timeIntervalSince1970
          userID: [messageObject objectForKey:@"userId"]
          isLocalUser: [[messageObject objectForKey:@"isLocalUser"] boolValue]];

      [conversation addObject:message];
    };
    
    MLKSmartReply *smartReply = [MLKSmartReply smartReply];
    [smartReply suggestRepliesForMessages:conversation
                               completion:^(MLKSmartReplySuggestionResult * _Nullable result,
                                            NSError * _Nullable error) {
      if (error || result == nil) {
        reject(@"failure", @"unknown error", nil);
      }
      if (result.status == MLKSmartReplyResultStatusNotSupportedLanguage) {
        reject(@"failure", @"unsupported language", nil);
      } else if (result.status == MLKSmartReplyResultStatusSuccess) {
        NSMutableArray* suggestions = [NSMutableArray array];

        for (MLKSmartReplySuggestion *suggestion in result.suggestions) {
          [suggestions addObject:suggestion.text];
        }

        resolve(suggestions);
      }
    }];
  });
}


@end
