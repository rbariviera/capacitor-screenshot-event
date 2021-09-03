import type { PluginListenerHandle } from '@capacitor/core';

export interface ScreenshotEventPlugin {
  startWatchEvent(): Promise<void>;
  removeWatchEvent(): Promise<void>;

  addListener(
    eventName: 'userDidTakeScreenshot',
    listenerFunc: (data: any) => void,
  ): PluginListenerHandle;
}
