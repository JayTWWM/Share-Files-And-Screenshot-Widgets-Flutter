#import "ShareFilesAndScreenshotWidgetsPlugin.h"
#if __has_include(<share_files_and_screenshot_widgets/share_files_and_screenshot_widgets-Swift.h>)
#import <share_files_and_screenshot_widgets/share_files_and_screenshot_widgets-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "share_files_and_screenshot_widgets-Swift.h"
#endif

@implementation ShareFilesAndScreenshotWidgetsPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftShareFilesAndScreenshotWidgetsPlugin registerWithRegistrar:registrar];
}
@end
