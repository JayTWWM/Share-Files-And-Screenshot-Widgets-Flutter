import 'dart:async';
import 'dart:io';
import 'dart:typed_data';
import 'dart:ui' as ui;
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';

class ShareFilesAndScreenshotWidgets {
  static const MethodChannel _channel =
      const MethodChannel('channel:share_files_and_screenshot_widgets');

  /// for share related text
  static void text(String title, String text, String mimeType) {
    Map argsMap = <String, String>{
      'title': '$title',
      'text': '$text',
      'mimeType': '$mimeType'
    };
    _channel.invokeMethod('text', argsMap);
  }

  /// writing file in temporary directory
  static Future<void> file(
      String title, String name, List<int> bytes, String mimeType,
      {String text = ''}) async {
    Map argsMap = <String, String>{
      'title': '$title',
      'name': '$name',
      'mimeType': '$mimeType',
      'text': '$text'
    };

    final tempDir = await getTemporaryDirectory();
    final file = await new File('${tempDir.path}/$name').create();
    await file.writeAsBytes(bytes);

    _channel.invokeMethod('file', argsMap);
  }

  /// writing files in temporary directory
  static Future<void> files(
      String title, Map<String, List<int>> files, String mimeType,
      {String text = ''}) async {
    Map argsMap = <String, dynamic>{
      'title': '$title',
      'names': files.entries.toList().map((x) => x.key).toList(),
      'mimeType': mimeType,
      'text': '$text'
    };

    final tempDir = await getTemporaryDirectory();

    for (var entry in files.entries) {
      final file = await new File('${tempDir.path}/${entry.key}').create();
      await file.writeAsBytes(entry.value);
    }

    _channel.invokeMethod('files', argsMap);
  }

  /// takes screenshot of the widget and returns Image
  Future<Image?> takeScreenshot(
      GlobalKey previewContainer, int originalSize) async {
    final currentContext = previewContainer.currentContext;
    if (currentContext == null) {
      return null;
    }
    RenderRepaintBoundary boundary =
        currentContext.findRenderObject() as RenderRepaintBoundary;
    double pixelRatio = originalSize / MediaQuery.of(currentContext).size.width;
    ui.Image image = await boundary.toImage(pixelRatio: pixelRatio);
    ByteData? byteData =
        await (image.toByteData(format: ui.ImageByteFormat.png));
    Uint8List pngBytes = byteData!.buffer.asUint8List();
    return Image.memory(pngBytes.buffer.asUint8List());
  }

  /// takes screenshot and invokes share
  shareScreenshot(GlobalKey previewContainer, int originalSize, String title,
      String name, String mimeType,
      {String text = ''}) async {
    final currentContext = previewContainer.currentContext;
    if (currentContext == null) {
      return;
    }
    RenderRepaintBoundary boundary =
        currentContext.findRenderObject() as RenderRepaintBoundary;
    double pixelRatio = originalSize / MediaQuery.of(currentContext).size.width;
    ui.Image image = await boundary.toImage(pixelRatio: pixelRatio);
    ByteData? byteData =
        await (image.toByteData(format: ui.ImageByteFormat.png));
    Uint8List pngBytes = byteData!.buffer.asUint8List();
    try {
      await file(title, name, pngBytes, mimeType, text: text);
    } catch (e) {
      print('error while sharing: $e');
    }
  }

  /// share any type of file using this function
  shareFile(String title, String name, Uint8List bytes, String mimeType,
      {String text = ''}) async {
    try {
      await file(title, name, bytes, mimeType, text: text);
    } catch (e) {
      print('error while sharing: $e');
    }
  }
}
