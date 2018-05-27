

package com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects;

/**
 * Generic item to create a sectionizable item
 */
public interface MPDGenericItem {

    /**
     * Generic function to get a string used for section indexing. Like the first letter of a title.
     *
     * @return The section title used for sectioning in the GUI.
     */
    String getSectionTitle();
}
