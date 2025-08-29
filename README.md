# PredictionEngine Algorithm Explained

This document explains the algorithm used in the `PredictionEngine` to predict video resolution choices on Android devices.

### **Objective:** To create a predictive model that accurately simulates how different Android devices choose a video resolution when their hardware doesn't support the exact size requested.

Our research has revealed that a device's decision is not a single calculation, but a hierarchical process of rules and constraints. Our latest algorithm now models this complex behavior with high accuracy.

Here is a breakdown of what the code is doing.

---

### **The Core Discovery: It's a Multi-Stage Funnel with Limitations**

We've learned that a device doesn't just pick the "closest" resolution. It follows a strict priority list to find a match, and critically, it has built-in limitations to protect itself from excessive processing load.

Our algorithm simulates this logic in a two-stage process.

#### **Stage 1: Finding the Best _Candidate_ Resolution**

The code first tries to find the best possible match from the camera's list of supported sizes (`availableResolutions`). It does this by stepping through a chain of rules. If a rule fails, it moves to the next one.

1.  **Exact Match:** The highest priority. If the device supports the exact `width` and `height` requested, it will always be chosen.
    
2.  **Width Match (with Tolerance):** The device then tries to match the requested **width**.
    -   **The Rule:** It looks for a resolution with the same width and the closest height.
    -   **The Limitation (New Discovery):** We learned devices have an **"Upscaling Tolerance."** They will only accept a match if it doesn't significantly increase the total number of pixels. Our code models this with a `UPSCALE_TOLERANCE` of 50%. It will reject a match if the new resolution is more than 50% larger than what was requested.
        
3.  **Height Match (with Tolerance):** If no suitable width match is found, the device tries to match the requested **height**.
    -   **The Rule:** It looks for a resolution with the same height and the closest width.
    -   **The Limitation (New Discovery):** This step is also governed by the **Upscaling Tolerance**. This is precisely what we learned from the OnePlus 4 vs. OnePlus 5 tests.
        -   **OnePlus 5 (Accepted):** A request for `480x360` was matched with `640x360`. This was a 33% pixel increase, which is _within_ the tolerance, so the match was accepted.
        -   **OnePlus 4 (Rejected):** A request for `480x360` had a potential height match of `792x360`. This was a 65% pixel increase, which is _outside_ the tolerance. The device rejected this match and moved to the next rule.
            
4.  **Best-Effort Downgrade:** If all prior matches fail or are rejected, the device plays it safe. It finds the largest available resolution that is **smaller than or equal to** the requested resolution. This is the step the OnePlus 4 ultimately used when it selected `352x288`.
    
5.  **Ultimate Failsafe:** If all else fails, it defaults to the absolute smallest resolution the device supports.
    

---
#### **Stage 2: The Final, Non-Negotiable Encoder Check**

After the best candidate resolution has been selected in Stage 1, it is passed to a final validation step that simulates the hardware video encoder.

-   **The Rule: Macroblock Alignment:** Video encoders are most efficient when a video's width and height are perfectly divisible by 16.
-   **The Action:** Our `validateForEncoder` function checks the selected resolution. If either dimension is not a multiple of 16, it adjusts it _down_ to the nearest one. For example, `640x360` becomes `640x352`.

This is the final, mandatory adjustment that explains many of the "weird" resolutions we see in practice.

### **Conclusion**

In summary, what we are doing is not just finding the "closest" resolution. We are simulating a sophisticated decision tree that weighs multiple factors in order of importance: **an exact match is best, followed by a non-excessive dimensional match (width then height), followed by a safe downgrade.** Finally, we apply the non-negotiable hardware constraints of the video encoder.

This multi-layered approach, refined through real-world device testing, has resulted in a predictive model that is far more robust and accurate than a simple mathematical "closest match" calculation.

---

[View Prediction Output](PREDICTION_OUTPUT.md)
