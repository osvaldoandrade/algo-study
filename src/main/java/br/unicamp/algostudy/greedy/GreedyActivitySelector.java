package br.unicamp.algostudy.greedy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Stack;

public class GreedyActivitySelector {

    private static final int N = 500000;

    public static void main(String[] args) {

	GreedyActivitySelector selector = new GreedyActivitySelector();
	//Generate N randomly activities
	List<Activity> activities = selector.setup(N);

	//n*log(n) + n time
	selector.evaluate(activities);

	//Linear time --Debug only
	selector.writeActivities(activities);
    }

    private List<Activity> setup(long size) {
	List<Activity> activities = new ArrayList<Activity>();
	Random r = new Random();

	for (long i = 0; i < size; i++) {
	    short r1 = (short) r.nextInt(24);
	    short r2 = (short) r.nextInt(24);

	    if (r1 <= r2) {
		activities.add(new Activity(i, r1, r2));
	    } else {
		activities.add(new Activity(i, r2, r1));
	    }
	}

	return activities;
    }

    //Total complexity is n*log(n) + n
    private void evaluate(List<Activity> activities) {

	//Sort STARTING date and ENDING times in n*log(n) time
	List<ActivityDetail> sortedS = new ArrayList<ActivityDetail>();
	List<ActivityDetail> sortedF = new ArrayList<ActivityDetail>();

	for (Activity act : activities) {
	    sortedS.add(new ActivityDetail(act.id, 0, act.start, act));
	    sortedF.add(new ActivityDetail(act.id, 1, act.end, act));
	}

	Collections.sort(sortedS);
	Collections.sort(sortedF);

	List<ActivityDetail> sorted = new ArrayList<ActivityDetail>();
	ListIterator<ActivityDetail> S = sortedS.listIterator();
	ListIterator<ActivityDetail> F = sortedF.listIterator();
	ActivityDetail s = S.next();
	ActivityDetail f = F.next();
	while (s != null || f != null) {
	    if (s == null) {
		while (f != null || F.hasNext()) {
		    sorted.add(f);
		    if (F.hasNext()) {
			f = F.next();
		    } else {
			f = null;
		    }
		}
		continue;
	    }

	    if (f == null) {
		while (s != null || S.hasNext()) {
		    sorted.add(s);
		    if (S.hasNext()) {
			s = S.next();
		    } else {
			s = null;
		    }
		}
		continue;
	    }

	    if (s.hour <= f.hour) {
		sorted.add(s);
		if (S.hasNext()) {
		    s = S.next();
		} else {
		    s = null;
		}
	    } else {
		sorted.add(f);
		if (F.hasNext()) {
		    f = F.next();
		} else {
		    f = null;
		}
	    }
	}
	
	sortedF = null;
	sortedS = null;

	// Allocating resources in linear Time
	Stack<Integer> resources = new Stack<Integer>();
	int resourceCount = 0;
	for (int i = 0; i < sorted.size(); i++) {
	    ActivityDetail c = sorted.get(i);
	    if (c.type == 0) {
		if (resources.isEmpty()) {
		    c.activity.addResource(++resourceCount);
		} else {
		    c.activity.addResource(resources.pop());
		}
	    } else {
		resources.add(c.activity.resource);
	    }
	}
    }

    private void writeActivities(List<Activity> activities) {
	for (Activity activity : activities) {
	    System.out.print("[");
	    int c = 0;
	    while (c < 24) {
		if (c >= activity.start && c <= activity.end) {
		    System.out.print(" # ");
		} else {
		    System.out.print(" - ");
		}
		c++;
	    }
	    System.out.print("] " + "Alocated resource: " + activity.resource + " Start: " + activity.start + " End: " + activity.end + "\n");
	}
    }

    public class Activity {

	long id;
	short start;
	short end;
	int resource;

	public Activity(long id, short start, short end) {
	    if ((start > 24 || end > 24) || (start > end)) {
		throw new RuntimeException("Invalid activity");
	    }

	    this.id = id;
	    this.start = start;
	    this.end = end;
	}

	public void addResource(int resource) {
	    this.resource = resource;
	}
    }

    public class ActivityDetail implements Comparable<ActivityDetail> {

	long id;
	int type;
	short hour;
	Activity activity;

	public ActivityDetail(long id, int type, short hour, Activity activity) {
	    this.id = id;
	    this.type = type;
	    this.hour = hour;
	    this.activity = activity;
	}

	@Override
	public int compareTo(ActivityDetail t) {
	    final int BEFORE = -1;
	    final int EQUAL = 0;
	    final int AFTER = 1;

	    if (this.hour > t.hour) {
		return AFTER;
	    }

	    if (this.hour < t.hour) {
		return BEFORE;
	    }

	    if (this.hour == t.hour) {
		return EQUAL;
	    }

	    return 0;
	}
    }
}
