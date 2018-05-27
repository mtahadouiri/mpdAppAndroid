

package com.mtdev.musicbox.application.listviewitems;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import com.mtdev.musicbox.application.adapters.ScrollSpeedAdapter;
import com.mtdev.musicbox.application.artworkdatabase.ArtworkManager;
import com.mtdev.musicbox.application.utils.AsyncLoader;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDGenericItem;


public abstract class AbsImageListViewItem extends RelativeLayout implements CoverLoadable {
    private static final String TAG = AbsImageListViewItem.class.getSimpleName();
    protected final ImageView mImageView;
    protected final ViewSwitcher mSwitcher;

    private Bitmap mBitmap;

    private AsyncLoader mLoaderTask;
    protected boolean mCoverDone = false;

    protected final AsyncLoader.CoverViewHolder mHolder;


    public AbsImageListViewItem(Context context, int layoutID, int imageviewID, int switcherID, ScrollSpeedAdapter adapter) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutID, this, true);

        mImageView = findViewById(imageviewID);
        mSwitcher = findViewById(switcherID);

        mHolder = new AsyncLoader.CoverViewHolder();
        mHolder.coverLoadable = this;
        mHolder.mAdapter = adapter;
        mHolder.imageDimension = new Pair<>(0,0);

        mCoverDone = false;
        if ( null != mImageView && null != mSwitcher) {
            mSwitcher.setOutAnimation(null);
            mSwitcher.setInAnimation(null);
            mImageView.setImageDrawable(null);
            mSwitcher.setDisplayedChild(0);
            mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            mSwitcher.setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        }
    }

    public void setImageDimension(int width, int height) {
        mHolder.imageDimension = new Pair<>(width, height);
    }

    /**
     * Starts the image retrieval task
     */
    public void startCoverImageTask() {
        if (mLoaderTask == null && mHolder.artworkManager != null && mHolder.modelItem != null && !mCoverDone) {
            mLoaderTask = new AsyncLoader();
            mLoaderTask.execute(mHolder);
        }
    }


    /**
     * Prepares the view to load an image when the scrolling view deems it is ready (scrollspeed slow enough).
     * @param artworkManager ArtworkManager instance used to get the image.
     * @param modelItem ModelItem to get the image for (MPDAlbum/MPDArtist)
     */
    public void prepareArtworkFetching(ArtworkManager artworkManager, MPDGenericItem modelItem) {
        if (!modelItem.equals(mHolder.modelItem)) {
            setImage(null);
        }
        mHolder.artworkManager = artworkManager;
        mHolder.modelItem = modelItem;
    }

    /**
     * If this GridItem gets detached from the parent it makes no sense to let
     * the task for image retrieval running. (non-Javadoc)
     *
     * @see android.view.View#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mLoaderTask != null) {
            mLoaderTask.cancel(true);
            mLoaderTask = null;
        }
    }

    /**
     * Sets the image of this view with a smooth fading animation.
     * If null is supplied it will reset the cover placeholder image.
     * @param image Image to show inside the view. null will result in the placeholder being shown.
     */
    public void setImage(Bitmap image) {
        mBitmap = image;
        if ( null == mImageView || null == mSwitcher) {
            return;
        }

        if (null != image) {
            mCoverDone = true;

            mImageView.setImageBitmap(image);
            mSwitcher.setDisplayedChild(1);
        } else {
            // Cancel old task
            if (mLoaderTask != null) {
                mLoaderTask.cancel(true);
            }
            mLoaderTask = null;
            mHolder.modelItem = null;

            mCoverDone = false;
            mSwitcher.setOutAnimation(null);
            mSwitcher.setInAnimation(null);
            mImageView.setImageDrawable(null);
            mSwitcher.setDisplayedChild(0);
            mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
            mSwitcher.setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
