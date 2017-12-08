package com.coremobile.coreyhealth.surveylog;


import java.util.Comparator;


/**
 * A collection of {@link Comparator}s for {@link SurveyModel} objects.
 *
 * @author Aman
 */
public final class SurveyLogComparators {

    private SurveyLogComparators() {
        //no instance
    }

    public static Comparator<SurveyModel> getPateintNameComparator() {
        return new PatientNameComparator();
    }

    public static Comparator<SurveyModel> getSurgeonComparator() {
        return new SurgeonComparator();
    }

    public static Comparator<SurveyModel> getDOSurgeryComparator() {
        return new DOSurgeryComparator();
    }

    public static Comparator<SurveyModel> getDOSurveyComparator() {
        return new DOSurveyComparator();
    }


    private static class PatientNameComparator implements Comparator<SurveyModel> {

        @Override
        public int compare(final SurveyModel surveyModel1, final SurveyModel surveyModel2) {
            return surveyModel1.getPatientName().compareTo(surveyModel2.getPatientName());
        }
    }

    private static class SurgeonComparator implements Comparator<SurveyModel> {

        @Override
        public int compare(final SurveyModel surveyModel1, final SurveyModel surveyModel2) {
            return surveyModel1.getSurgeon().compareTo(surveyModel2.getSurgeon());
        }
    }

    private static class DOSurgeryComparator implements Comparator<SurveyModel> {

        @Override
        public int compare(final SurveyModel surveyModel1, final SurveyModel surveyModel2) {
            return surveyModel1.getDos().compareTo(surveyModel2.getDos());
        }
    }

    private static class DOSurveyComparator implements Comparator<SurveyModel> {

        @Override
        public int compare(final SurveyModel surveyModel1, final SurveyModel surveyModel2) {
            return surveyModel1.getDateofsurvey().compareTo(surveyModel2.getDateofsurvey());
        }
    }

}
