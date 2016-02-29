package nu.geeks.uio_kth.Objects;

import java.util.ArrayList;
import java.util.Collections;
import static java.lang.Math.abs;
import static java.lang.Math.min;


public class Algorithms {

    float cutoffForConsideredEqual = 0.1f; // ignores smaller diffs than 0.1
    private ArrayList<Person> creditors;
    private ArrayList<Person> debtors;
    private ArrayList<Payment> payments;

    /**
     * Returns a list of settlement payments: from, to and amount
     * @param personWithPayedAmount The total amount payed by person
     * @return
     */
    public ArrayList<Payment> calculatePayments(ArrayList<Person> personWithPayedAmount) {
        // calculate balances
        ArrayList<Person> amountsToSettle = calculateAmountsToSettle(personWithPayedAmount);

        // settle
        ArrayList<Payment> payments = settle(amountsToSettle);
        for (Payment p : payments) {
            p.amount = (float) Math.round(p.amount);
        }
        System.out.println("PAYMENTS ROUNDED:");
        statusp(payments);

        // for verification
        System.out.println("Cred:");
        status(creditors);
        System.out.println("Debt:");
        status(debtors);
        return payments;
    }

    private ArrayList<Person> calculateAmountsToSettle(ArrayList<Person> personsWithPayedAmount) {
        // Sum up and divide, to find settlement amounts
        int persons = personsWithPayedAmount.size();
        float sum = 0;
        for (Person p : personsWithPayedAmount) {
            sum += p.amount;
        }
        float perPerson = sum / (float) persons;

        ArrayList<Person> amountsOwed = new ArrayList<>();
        for (Person p : personsWithPayedAmount) {
            float amountOwed = perPerson - p.amount;
            Person newPerson = new Person(p.name, amountOwed);
            amountsOwed.add(newPerson);
        }
        return amountsOwed;
    }

    private ArrayList<Payment> settle(ArrayList<Person> amountsToSettle) {
        status(amountsToSettle);

        // setup
        creditors = new ArrayList();
        debtors = new ArrayList();
        payments = new ArrayList<>();
        for (Person p : amountsToSettle) {
            if (p.amount < 0) {
                creditors.add(p);
            } else if (p.amount > 0) {
                debtors.add(p);
            }
        }

        boolean done;
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            done = false;

            /*
             SORTING DEBTORS AND CREDITORS
             */
            Collections.sort(creditors, Collections.reverseOrder());
            Collections.sort(debtors, Collections.reverseOrder());

            System.out.println("Cred:");
            status(creditors);
            System.out.println("Debt:");
            status(debtors);

            // STEP 1: EQUALS
            done = removeEquals();

            // STEP 2: 2 DEBTORS EQUALS CREDITOR
            if (!done) {
                done = removeThrees();
            }

            // STEP 3: REMOVE BIGGEST
            if (!done && !creditors.isEmpty()) {
                removeBiggest();
            }
            statusp(payments);
        }
        return payments;
    }

    /*
     * STEG 1: Lika värden
     */
    private boolean removeEquals() {
        boolean done = false;

        for (Person c : creditors) {
            if (!c.done) {
                for (Person d : debtors) {
                    if (!d.done) {
                        if (abs((c.amount + d.amount)) < cutoffForConsideredEqual) { // IMPORTANT CUTOFF!
                            System.out.println("------- STEP 1: Lika värden funna: " + c.amount);
                            payments.add(new Payment(d.name, c.name, d.amount));
                            c.done = true;
                            d.done = true;
                            done = true;
                            break;
                        }
                    }
                }
            } else {
                break;
            }
        }
        deletions();
        return done;
    }

    /*
     * STEG 2. Summera ihop två skuldsatta & jämföra med kreditorer
     */
    private boolean removeThrees() {
        boolean done = false;

        if (!creditors.isEmpty() && debtors.size() > 1) {
            for (Person c : creditors) {
                for (Person d : debtors) {
                    for (int i = 1; i < debtors.size(); i++) {
                        if (!done) {
                            if ((((d.amount + debtors.get(i).amount) - c.amount) < cutoffForConsideredEqual) && !d.isSame(debtors.get(i).name)) {
                                payments.add(new Payment(d.name, c.name, d.amount));
                                payments.add(new Payment(debtors.get(i).name, c.name, debtors.get(i).amount));
                                c.done = true;
                                d.done = true;
                                debtors.get(i).done = true;
                                done = true;
                                System.out.println("------- STEP 2: Två summerade debtors betalar en person: " + c.name + ", " + d.name + ", " + debtors.get(i).name);
                            }
                        } else {
                            break;
                        }
                    }
                    if (done) {
                        break;
                    }
                }
                if (done) {
                    break;
                }
            }
        }
        deletions();
        return done;
    }

    /*
     * STEG 3. Subtrahera de största
     */
    private void removeBiggest() {
        Person c = creditors.get(0);
        Person d = debtors.get(0);
        float credAmount = abs(c.amount);
        float debtAmount = d.amount;

        float amount = min(credAmount, debtAmount);

        c.amount = c.amount + amount;
        d.amount = debtAmount - amount;
        System.out.println("------- STEP 3, settled amount: " + amount);
        payments.add(new Payment(d.name, c.name, amount));
        if (c.amount == 0) {
            creditors.remove(c);
        } else {
            debtors.remove(d);
        }
    }

    private void  deletions() {
        ArrayList<Person> delete = new ArrayList();
        for (Person d : debtors) {
            if (d.done) {
                delete.add(d);
            }
        }
        for (Person d : delete) {
            debtors.remove(d);
        }
        delete.clear();
        for (Person c : creditors) {
            if (c.done) {
                delete.add(c);
            }
        }
        for (Person c : delete) {
            creditors.remove(c);
        }
    }

    private void status(ArrayList<Person> debts) {
        for (Person p : debts) {
            System.out.println(p.name + "'s balance is: " + p.amount);
        }
    }

    private void statusp(ArrayList<Payment> debts) {
        System.out.println();
        System.out.println("PAYMENTS:");
        for (Payment p : debts) {
            System.out.println(p.from + " --> " + p.to + " " + p.amount + " SEK");
        }
        System.out.println();
    }
}
