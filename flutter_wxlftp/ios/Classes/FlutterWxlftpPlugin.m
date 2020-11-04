#import "FlutterWxlftpPlugin.h"
#if __has_include(<flutter_wxlftp/flutter_wxlftp-Swift.h>)
#import <flutter_wxlftp/flutter_wxlftp-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_wxlftp-Swift.h"
#endif

@implementation FlutterWxlftpPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterWxlftpPlugin registerWithRegistrar:registrar];
}
@end
