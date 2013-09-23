# DAT076 Project

A web app project for the [DAT076 course](http://www.cse.chalmers.se/edu/course/DAT076/) at Chalmers University of Technology.

## Develop

Always use the `develop` branch as trunk, and use feature/bug branches as needed. 

Sample workflow when starting a new coding session:

1. Do a `git pull` on `develop`.
2. `checkout` your dev branch and rebase with `develop`.
3. Code.

Before pushing to `develop`, do the following:

1. Do a `git pull` on `develop`.
2. `checkout` your dev branch and rebase with `develop`. Fix any merge conflicts *locally in your local dev branch* (not `develop`).
3. `git checkout develop` and merge with your dev branch.
4. Push to `develop`.

**Only push to `master` when having stable working builds** (they should pass all tests).