package com.coremobile.coreyhealth.patientactivitylog;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.coremobile.coreyhealth.R;
import com.coremobile.coreyhealth.surveylog.SurveyModel;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;


/**
 * An extension of the {@link SortableTableView} that handles {@link SurveyModel}s.
 *
 * @author Aman
 */
public class SortablePatientLogTableView extends SortableTableView<ActivityModel> {

    public SortablePatientLogTableView(final Context context) {
        this(context, null);
    }

    public SortablePatientLogTableView(final Context context, final AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public SortablePatientLogTableView(final Context context, final AttributeSet attributes, final int styleAttributes) {
        super(context, attributes, styleAttributes);

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context,"Patient Name","D.O.S.","Last Login","First Invite","Reminders");
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(context, R.color.table_header_text));
        setHeaderAdapter(simpleTableHeaderAdapter);

        final int rowColorEven = ContextCompat.getColor(context, R.color.table_data_row_even);
        final int rowColorOdd = ContextCompat.getColor(context, R.color.table_data_row_odd);
        setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(rowColorEven, rowColorOdd));
        setHeaderSortStateViewProvider(SortStateViewProviders.brightArrows());

        final TableColumnWeightModel tableColumnWeightModel = new TableColumnWeightModel(5);
        //tableColumnWeightModel.setColumnWeight(0, 2);
        tableColumnWeightModel.setColumnWeight(0, 3);
        tableColumnWeightModel.setColumnWeight(1, 2);
        tableColumnWeightModel.setColumnWeight(2, 3);
        tableColumnWeightModel.setColumnWeight(3, 3);
        tableColumnWeightModel.setColumnWeight(4, 2);

        setColumnModel(tableColumnWeightModel);

        setColumnComparator(0, PatientLogComparators.getPateintNameComparator());
        setColumnComparator(2, PatientLogComparators.getLastLoginComparator());
        setColumnComparator(1, PatientLogComparators.getDOSurgeryComparator());
        setColumnComparator(3, PatientLogComparators.getFirstInviteComparator());

    }

}
