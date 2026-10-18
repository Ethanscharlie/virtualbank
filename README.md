# Virtual Bank
The purpose of this app is to manage money is ways that common bank apps do not allow. The main feature being "rules", a way to automatically move money based on conditions,

## Screenshots
<table>
  <tr>
    <td><img width="1080" height="2340" alt="Screenshot_20261013_223518_Virtual Bank" src="https://github.com/user-attachments/assets/b306b3e2-f38c-4f3d-9051-298f7fd5fd5e" /></td>
    <td><img width="1080" height="2340" alt="Screenshot_20261013_223533_Virtual Bank" src="https://github.com/user-attachments/assets/4ac11006-b4b1-4487-adcb-3c1fb1160f3d" /></td>
    <td><img width="1080" height="2340" alt="Screenshot_20261013_223536_Virtual Bank" src="https://github.com/user-attachments/assets/980c06d6-3380-4951-8890-7ea88c0bfa32" /></td>
  </tr>
</table>

## Rules
There are two different acceptable types of rules.
```
<period> <account> <value or %> <account>
```
```
<period> if <account> <inequality> <value> then <account> <value or %> <account>
```

Valid inequalitys are <, >, =, and !=

Valid periods are weekly, biweekly, and monthly

Rules will be evaluated by line, to have more then one rule, just put the next rule on the next line.

### Example 1
```
weekly if Savings < 1500 then Checking 10 Savings
```
This will move $10 from Checking into Savings if Savings has less than $1500


### Example 2
```
biweekly Checking 50% Savings
```
This will move 50% of Checking into Savings

## Compile Instructions
```
./gradlew build
```
Use `./buildandrun.sh` to use the android emulator with the android cli in `~/.android-cli/bin/`
