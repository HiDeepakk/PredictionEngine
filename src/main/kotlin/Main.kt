import model.DesiredConfig
import model.Device
import model.Resolution
import model.data.nothingAvailableResolutions
import model.data.onePlus4AvailableResolutions
import model.data.onePlus5AvailableResolutions
import model.data.pixel9ProAvailableResolutions
import model.data.pocoAvailableResolutions
import model.data.realmeAvailableResolutions

fun main() {
    // List of encoder configurations
    val encoderConfigs = listOf(
        DesiredConfig("h90p_w160p", Resolution(160, 90)),
        DesiredConfig("h180p_w320p", Resolution(320, 180)),
        DesiredConfig("h216p_w384p", Resolution(384, 216)),
        DesiredConfig("h360p_w640p", Resolution(640, 360)),
        DesiredConfig("h540p_w960p", Resolution(960, 540)),
        DesiredConfig("h720p_w1280p", Resolution(1280, 720)),
        DesiredConfig("h1080p_w1920p", Resolution(1920, 1080)),
        DesiredConfig("h1440p_w2560p", Resolution(2560, 1440)),
        DesiredConfig("h2160p_w3840p", Resolution(3840, 2160)),
        DesiredConfig("h120p_w160p", Resolution(160, 120)),
        DesiredConfig("h180p_w240p", Resolution(240, 180)),
        DesiredConfig("h240p_w320p", Resolution(320, 240)),
        DesiredConfig("h360p_w480p", Resolution(480, 360)),
        DesiredConfig("h480p_w640p", Resolution(640, 480)),
        DesiredConfig("h540p_w720p", Resolution(720, 540)),
        DesiredConfig("h720p_w960p", Resolution(960, 720)),
        DesiredConfig("h1080p_w1440p", Resolution(1440, 1080)),
        DesiredConfig("h1440p_w1920p", Resolution(1920, 1440))
    )

    // List of devices with their available resolutions
    val devices = listOf(
        Device("Pixel", pixel9ProAvailableResolutions),
        Device("OnePlus5", onePlus5AvailableResolutions),
        Device("Realme", realmeAvailableResolutions),
        Device("Poco", pocoAvailableResolutions),
        Device("OnePlus4", onePlus4AvailableResolutions),
        Device("Nothing", nothingAvailableResolutions)
    )


    // Run predictions for each config and device
    for (config in encoderConfigs) {
        println("Running predictions for config: ${config.name} (${config.resolution.width}x${config.resolution.height})")
        for (device in devices) {
            println("Testing on ${device.name}...")
            PredictionEngine.predictSelectedResolution(config, device.availableResolutions)
        }
        println("\n")
    }
}
