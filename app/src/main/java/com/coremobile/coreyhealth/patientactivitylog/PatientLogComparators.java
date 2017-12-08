package com.coremobile.coreyhealth.patientactivitylog;


import java.util.Comparator;


/**
 * A collection of {@link Comparator}s for {@link ActivityModel} objects.
 *
 * @author Aman
 */
public final class PatientLogComparators {

    private PatientLogComparators() {
        //no instance
    }

    public static Comparator<ActivityModel> getPateintNameComparator() {
        return new PatientNameComparator();
    }


    public static Comparator<ActivityModel> getDOSurgeryComparator() {
        return new DOSurgeryComparator();
    }



    public static Comparator<ActivityModel> getLastLoginComparator() {
        return new LastLoginComparator();
    }
    public static Comparator<ActivityModel> getFirstInviteComparator() {
        return new FirstInviteComparator();
    }

    private static class PatientNameComparator implements Comparator<ActivityModel> {

        @Override
        public int compare(final ActivityModel ActivityModel1, final ActivityModel ActivityModel2) {
            return ActivityModel1.getPatientName().compareTo(ActivityModel2.getPatientName());
        }
    }


    private static class LastLoginComparator implements Comparator<ActivityModel> {

        @Override
        public int compare(final ActivityModel ActivityModel1, final ActivityModel ActivityModel2) {
            return ActivityModel1.getLastLogin().compareTo(ActivityModel2.getLastLogin());
        }
    }

    private static class FirstInviteComparator implements Comparator<ActivityModel> {

        @Override
        public int compare(final ActivityModel ActivityModel1, final ActivityModel ActivityModel2) {
            return ActivityModel1.getFirstInvitation().compareTo(ActivityModel2.getFirstInvitation());
        }
    }


    private static class DOSurgeryComparator implements Comparator<ActivityModel> {

        @Override
        public int compare(final ActivityModel ActivityModel1, final ActivityModel ActivityModel2) {
            return ActivityModel1.getDos().compareTo(ActivityModel2.getDos());
        }
    }

}
