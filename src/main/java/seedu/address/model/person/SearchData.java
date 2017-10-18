package seedu.address.model.person;

import seedu.address.commons.exceptions.IllegalValueException;

import static java.util.Objects.requireNonNull;

/***
 * @author Sri-vatsa
 */

public class SearchData {

    private String searchCount;

    /**
     *
     * @throws IllegalValueException if searchCount string is invalid.
     */
    public SearchData(String searchCount) throws IllegalValueException {
        this.searchCount = searchCount;
    }


    public void setSearchCount(String searchCount) {
        this.searchCount = searchCount;
    }

    public String getSearchCount() {
        return this.searchCount;
    }

    /**
     * Increases search count by 1 each time it is called
     */
    public void IncrementSearchCount() {
        int searchCountInt = Integer.parseInt(this.searchCount);
        searchCountInt++;
        this.searchCount = Integer.toString(searchCountInt);
    }

    @Override
    public String toString() {
        return searchCount;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Phone // instanceof handles nulls
                && this.searchCount.equals(((Phone) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return searchCount.hashCode();
    }


}
