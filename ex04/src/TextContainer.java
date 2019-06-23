public class TextContainer {

    /**
     * Appends new Pargraph object to Document.
     *
     * @param text the text of the new paragraph.
     */
    public void appendParagraph(String text) {
    }

    /**
     * Inserts a new Paragraph object directly after the specified paragraph.
     *
     * @param text the text of the new paragraph.
     * @param paragraphID the ID of the paragraph, after which the new paragraph should be inserted.
     * @return whether operation was successful.
     */
    public boolean insertAfterParagraph(String text, int paragraphID) {
        return false;
    }

    /**
     * Deletes the specified paragraph.
     *
     * @param paragraphID the ID of the paragraph to be deleted.
     * @return whether the operation was successful.
     */
    public boolean deleteParagraph(int paragraphID) {
        return false;
    }

    /**
     * Merges a paragraph with the following one. Here the text of the following
     * paragraph should be added to the specified one, and the following paragraph
     * should then be deleted.
     *
     * @param paragraphID the ID of the paragraph which should be merged with its following paragraph.
     * @return whether the operation was successful.
     */
    public boolean mergeParagraphs(int paragraphID) {
        return false;
    }

    /**
     * Adds a string the the text of a paragraph.
     *
     * @param text the text to be appended to the paragraph.
     * @param paragraphID the paragraph, whose text should be appended.
     * @return whether the operation was successful.
     */
    public boolean appendText(String text, int paragraphID) {
        return false;
    }

    /**
     * Replaces the text of a paragraph with the given string.
     *
     * @param text the new text of the paragraph.
     * @param paragraphID the ID of the paragraph, whose text should be replaced.
     * @return whether the operation was successful.
     */
    public boolean replaceText(String text, int paragraphID) {
        return false;
    }

    /**
     * Returns the text of the entire document as a single string. Each paragraph is appended
     * with a newline character ('\n').
     *
     * @return the entire text of the document
     */
    public String getText() {
        return null;
    }

}
