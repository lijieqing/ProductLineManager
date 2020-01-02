package hua.lee.plm.concurrent;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * HTML 渲染
 *
 * @author lijie
 * @create 2019-12-17 10:39
 **/
public class HtmlRender {
    @Test
    public void singleThreadRender() {
        CharSequence source = "";
        renderText(source);
        List<ImageData> imageDatas = new ArrayList<>();

        for (ImageInfo imageInfo : scanForImageInfo(source)) {
            imageDatas.add(imageInfo.downloadImage());
        }

        for (ImageData imageData : imageDatas) {
            renderImage(imageData);
        }

    }

    @Test
    public void futureRender() {
        CharSequence source = "";
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<ImageInfo> imageInfos = scanForImageInfo(source);
        Callable<List<ImageData>> task = new Callable<List<ImageData>>() {
            @Override
            public List<ImageData> call() throws Exception {
                List<ImageData> result = new ArrayList<>();
                for (ImageInfo imageInfo : imageInfos) {
                    result.add(imageInfo.downloadImage());
                }
                return result;
            }
        };

        Future<List<ImageData>> future = executor.submit(task);
        renderText(source);

        try {
            List<ImageData> imageDatas = future.get();
            for (ImageData imageData : imageDatas) {
                renderImage(imageData);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            future.cancel(true);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void completionServiceRender(ExecutorService executor, CharSequence source) {
        List<ImageInfo> info = scanForImageInfo(source);
        CompletionService<ImageData> completionService = new ExecutorCompletionService<>(executor);
        for (ImageInfo imageInfo : info) {
            completionService.submit(new Callable<ImageData>() {
                @Override
                public ImageData call() throws Exception {
                    return imageInfo.downloadImage();
                }
            });
        }
        renderText(source);

        try {
            int taskSize = info.size();
            for (int i = 0; i < taskSize; i++) {
                Future<ImageData> f = completionService.take();
                ImageData data = f.get();
                renderImage(data);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    private List<ImageInfo> scanForImageInfo(CharSequence source) {
        return null;
    }

    private void renderImage(ImageData imageData) {

    }

    private void renderText(CharSequence sourc) {

    }
}

class ImageData {
}

class ImageInfo {
    public ImageData downloadImage() {
        return null;
    }
}
