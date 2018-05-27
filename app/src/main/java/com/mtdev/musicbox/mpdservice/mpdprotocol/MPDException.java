

package com.mtdev.musicbox.mpdservice.mpdprotocol;

public class MPDException extends Exception {
    private String mError;

    public MPDException(String error) {
        mError = error;
    }

    public String getError() {
        return mError;
    }

    public static class MPDConnectionException extends MPDException {

        public MPDConnectionException(String error) {
            super(error);
        }
    }

    public static class MPDServerException extends MPDException {
        private static final String TAG = MPDServerException.class.getSimpleName();
        private int mErrorCode;
        private int mCommandOffset;
        private String mErrorMessage;
        private String mCommand;

        public MPDServerException(String error) {
            super(error);

            // Parse the error message (s. https://www.musicpd.org/doc/protocol/response_syntax.html#failure_response_syntax)
            String substring;
            // Start with the [ErrorCode@Offset]
            int subStringStart, subStringStop;

            subStringStart = error.indexOf('[');
            subStringStop = error.indexOf('@');

            // If subStringStop is -1 try the ], probably no offset sent (Quod Libet)
            if (subStringStop == -1) {
                subStringStop = error.indexOf(']');
            }

            if (subStringStart != -1 && subStringStop != -1 ) {
                substring = error.substring(error.indexOf('[') + 1, error.lastIndexOf('@'));
                try {
                    mErrorCode = Integer.valueOf(substring);
                } catch (NumberFormatException e) {
                    mErrorCode = -4711;
                }
            }

            subStringStart = error.indexOf('@');
            subStringStop = error.indexOf(']');

            if (subStringStart != -1 && subStringStop != -1 ) {
                substring = error.substring(error.indexOf('@') + 1, error.lastIndexOf(']'));
                try {
                    mCommandOffset = Integer.valueOf(substring);
                } catch (NumberFormatException e) {
                    mCommandOffset = -1;
                }
            }

            // Get the command from {command}

            subStringStart = error.indexOf('{');
            subStringStop = error.indexOf('}');
            if (subStringStart != -1 && subStringStop != -1 ) {
                substring = error.substring(error.indexOf('{') + 1, error.lastIndexOf('}'));
                mCommand = substring;
            }

            // Get the message from } on.
            subStringStart = error.indexOf('}');
            if (subStringStart != -1) {
                substring = error.substring(error.lastIndexOf('}') + 2, error.length());
                mErrorMessage = substring;
            }
        }

        public int getErrorCode() {
            return mErrorCode;
        }

        public int getCommandOffset() {
            return mCommandOffset;
        }

        public String getCommand() {
            return mCommand;
        }

        public String getServerMessage() {
            return mErrorMessage;
        }
    }
}
