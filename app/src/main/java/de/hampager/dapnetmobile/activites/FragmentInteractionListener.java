package de.hampager.dapnetmobile.activites;

/**
 * FragmentInteractionListener : For use in MainActivity to manage its View components (action bar title
 * and "FloatingActionButton" visibility) when interacting with Fragments.
 *
 * TODO: fix redundancy in initializations? (onAttach, onStart, onDetatch)
 */
public interface FragmentInteractionListener {

    /**
     * For handling View components in MainActivity container.
     *
     * @param fabVisible  Shows/hides "FloatingActionButton"
     * @param titleID  Name for action bar title
     */
    void onFragmentInteraction(boolean fabVisible, int titleID);
}
