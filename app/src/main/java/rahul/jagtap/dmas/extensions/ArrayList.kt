package rahul.jagtap.dmas.extensions

import rahul.jagtap.dmas.model.DayBookTableItem


fun ArrayList<DayBookTableItem>.addIfNotExists(item: DayBookTableItem) {
    if (this.contains(item)) return
    this.add(item)
}