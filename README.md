## Pure Native Components
Pure Native Components is an library that includes some components written in Jetpack Compose.
The bottom line is that these components were often encountered in our projects and it was decided to move them to a separate library 
for the convenience of current and future employees of the company and just people who it would be interesting to look at the functions created by Jetpack Compose.
Also, libraries of other open access users whose functionality we need have been and will be transferred here.

## Appearance, examples and description
At the moment the sample app looks like this:
![img.png](readme_pictures/img.png)

### [GifImageComponent](./sample/src/main/java/pn/android/gif_image_example/GifImageScreen.kt)
![img_4.png](readme_pictures/img_4.png)

The component is responsible for displaying your GIF
### [CalendarScreen](./sample/src/main/java/pn/android/calendar_example/CalendarScreen.kt)
![img_2.png](readme_pictures/img_2.png)

The component is designed to display a calendar. From the functionality, it is possible to determine the difference in days between two selected dates and clear the result
### [PrimaryIndicatorScreen](./sample/src/main/java/pn/android/primary_indicator_example/PrimaryIndicatorScreen.kt)
![img_3.png](readme_pictures/img_3.png)

The component is designed to display the loading status on the screen
### [ListItemPickerScreen](./sample/src/main/java/pn/android/list_item_picker_example/ListItemPickerScreen.kt)
![img_5.png](readme_pictures/img_5.png)

The component is designed to be able to select from a list of elements of a certain type
### [ImageGalleryScreen](./sample/src/main/java/pn/android/image_gallery_example/ImageGalleryScreen.kt)
![img_6.png](readme_pictures/img_6.png)

The component is designed to display a gallery of images with the ability to zoom in using a double click or by simultaneously spreading two fingers on the screen
### [MaskVisualTransformationScreen](./sample/src/main/java/pn/android/mask_visual_transformation_example/MaskVisualTransformationScreen.kt)
![img_7.png](readme_pictures/img_7.png)

The component is designed to control the input format
### [LazyColumnWithPagingScreen](./sample/src/main/java/pn/android/lazy_column_with_paging/LazyColumnWithPagingScreen.kt)
![img_8.png](readme_pictures/img_8.png)

The component is designed to display a list with a large amount of data. 
To spend much less resources on getting a large amount of data at once,
the component suggests dividing this data into smaller pieces and requesting further data when approaching the end of the list
## Modules

### [Core](./core/)
Common Android things.
### [Compose](./compose/)
Jetpack Compose things.
### [Sample](./sample/)
Sample App for research and development.

## Installation

### Gradle

Make sure you've added Kotlin support to your project.

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.purenative.pn-android:core:$latest_version'
    implementation 'com.github.purenative.pn-android:compose:$latest_version'
}
```
