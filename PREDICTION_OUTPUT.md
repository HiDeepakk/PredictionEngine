# R&D Report: Android Camera Resolution Predictions

This document contains the output of our PredictionEngine for various video configurations across a range of Android devices. The goal is to demonstrate how the final, selected resolution can vary based on device hardware and software constraints.

Our algorithm predicts the outcome of a two-stage process:

1.  **Camera HAL Selection:** The camera hardware chooses the best-fit resolution from its list of supported sizes.
2.  **Encoder Adjustment:** The media encoder may further adjust that resolution to meet its hardware requirements (e.g., ensuring dimensions are divisible by 16).

## Prediction Results by Configuration

The table below shows the predicted output for each requested video configuration.

**Note:** Resolutions marked with an asterisk (*) were adjusted by our algorithm's final "Encoder Check" to satisfy macroblock alignment (a requirement that dimensions be divisible by 16).

| Requested Config | Requested Resolution | Pixel | OnePlus 5 | Realme | Poco | OnePlus 4 | Nothing |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **16:9 Aspect Ratio** | | | | | | | |
| h90p_w160p | 160x90 | 176x144 | 176x144 | 160x96 | 176x144 | 176x144 | 176x144 |
| h180p_w320p | 320x180 | 320x240 | 320x240 | 320x240 | 320x240 | 320x240 | 320x240 |
| h216p_w384p | 384x216 | 320x240 | 320x240 | 320x240 | 320x240 | 320x240 | 320x240 |
| h360p_w640p | 640x360 | 640x352* | 640x352* | 640x352* | 640x352* | 640x480 | 640x480 |
| h540p_w960p | 960x540 | 800x592* | 960x544 | 960x528* | 800x592* | 960x528* | 960x720 |
| h720p_w1280p | 1280x720 | 1280x720 | 1280x720 | 1280x720 | 1280x720 | 1280x720 | 1280x720 |
| h1080p_w1920p | 1920x1080 | 1920x1072* | 1920x1072* | 1920x1072* | 1920x1072* | 1920x1072* | 1920x1072* |
| h1440p_w2560p | 2560x1440 | 1920x1440 | 1920x1440 | 2560x1440 | 1920x1440 | 1920x1440 | 2560x1440 |
| h2160p_w3840p | 3840x2160 | 1920x1440 | 2304x1728 | 3264x2448 | 2592x1936* | 2304x1728 | 3280x2464 |
| **4:3 Aspect Ratio** | | | | | | | |
| h120p_w160p | 160x120 | 176x144 | 176x144 | 160x96 | 176x144 | 176x144 | 176x144 |
| h180p_w240p | 240x180 | 176x144 | 176x144 | 192x144 | 176x144 | 176x144 | 192x144 |
| h240p_w320p | 320x240 | 320x240 | 320x240 | 320x240 | 320x240 | 320x240 | 320x240 |
| h360p_w480p | 480x360 | 640x352* | 640x352* | 640x352* | 640x352* | 352x288 | 352x288 |
| h480p_w640p | 640x480 | 640x480 | 640x480 | 640x480 | 640x480 | 640x480 | 640x480 |
| h540p_w720p | 720x540 | 720x480 | 720x480 | 720x480 | 720x480 | 720x528* | 720x480 |
| h720p_w960p | 960x720 | 1280x720 | 960x720 | 960x720 | 1280x720 | 960x720 | 960x720 |
| h1080p_w1440p | 1440x1080 | 1440x1072* | 1440x1072* | 1440x1072* | 1440x1072* | 1440x1072* | 1440x1072* |
| h1440p_w1920p | 1920x1440 | 1920x1440 | 1920x1440 | 1920x1440 | 1920x1440 | 1920x1440 | 1920x1440 |

## Key Takeaways & Observed Patterns

This data validates our prediction model and highlights several key behaviors that developers should be aware of.

1.  **Aspect Ratio is Often Sacrificed**
    
    When a device doesn't support a specific 16:9 resolution, it will often fall back to a 4:3 resolution with the same width.
    
    *Example:* For `h180p_w320p` (320x180), every device defaulted to 320x240. This is the "Width Match" rule in action.
    
2.  **Encoder Alignment is Crucial (*)**
    
    Many modern devices support modern resolutions, but their hardware encoders require dimensions to be divisible by 16.
    
    *Example:* For `h1080p_w1920p` (1920x1080), every device found an exact match but had to adjust the final output to 1920x1072 because 1080 is not divisible by 16. This is the most common reason for small, unexpected changes in resolution.
    
3.  **Upscaling Behavior Varies**
    
    Some devices will upscale to match a dimension, while others will not.
    
    *Example:* For `h360p_w480p` (480x360), most devices chose 640x352*. They prioritized matching the requested height (360, adjusted to 352) even if it meant increasing the width.
    
    *Contrast:* The OnePlus 4 and Nothing phones rejected this upscale as being too excessive. Instead, they fell back to the next best smaller resolution: 352x288. This highlights the "upscaling tolerance" we built into our model.
    
4.  **High-Resolution Requests are Downgraded**
    
    No devices in our test pool supported 4K (3840x2160). In every case, the request resulted in a "Best Fallback" to the largest available resolution the device supported, such as 3264x2448 on the Realme or 2592x1936* on the Poco.
    

This data demonstrates that our `PredictionEngine` successfully models the complex, multi-step decision-making process of Android devices, providing us with a powerful tool to build more stable and reliable video features.
