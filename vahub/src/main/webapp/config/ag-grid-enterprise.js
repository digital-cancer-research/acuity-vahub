const { exec } = require('child_process');
const fs = require('fs');
const path = require('path');

const agGridEnterprisePackageName = 'ag-grid-enterprise';
const agGridEnterprisePackageVersion = '@12.0.2';

const moduleStubPath = `./node_modules/${agGridEnterprisePackageName}/main.js`;

const COMMAND_TYPES = {
    install: 'install',
    uninstall: 'uninstall'
}

const args = process.argv.slice(2);

if (args[0] === "on") {
    enableAgGridEnterprise();
} else {
    disableAgGridEnterprise();
}

function enableAgGridEnterprise() {
    // install ag-grid-enterprise dependency
    const packageName = agGridEnterprisePackageName + agGridEnterprisePackageVersion;
    console.log(`installing ${packageName}...`);

    execPackageCommand(packageName, COMMAND_TYPES.install).then(() => {
        console.log(`${packageName} installed successfully`);
    });
}

function disableAgGridEnterprise() {
    // uninstall ag-grid-enterprise dependency
    console.log(`uninstalling ${agGridEnterprisePackageName}...`);

    execPackageCommand(agGridEnterprisePackageName, COMMAND_TYPES.uninstall)
        .then(() => {
            // we need to create module stub, otherwise TypeScript won't compile due to import of non-existing module
            createModuleStub(moduleStubPath);
        }).catch((err) => {
            console.log(err);
        });
}

function execPackageCommand(packageName, commandType) {
    let command; 

    switch (commandType) {
        case COMMAND_TYPES.install:
            command = `npm install ${packageName} --no-save`;
            break;
        case COMMAND_TYPES.uninstall:
            command = `npm uninstall ${packageName}`;
            break;
        default:
            return;
    }

    console.log(command);

    const execPromise = new Promise((resolve, reject) => {
        exec(command, (err, stdout, stderr) => {
            if (err) {
                console.error(err);
                reject();
            } else {
                console.log(stdout);
                console.log(stderr);
                resolve();
            }
       });
    });

    return execPromise;
}

function createModuleStub(stubFilePath) {
    const dir = path.dirname(stubFilePath);
    
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir);

        fs.writeFileSync(stubFilePath, `export const LicenseManager = {};`);
        fs.writeFileSync(dir + '/package.json', 
`{
    "name": "ag-grid-enterprise",
    "version": "1.0.0"
}`);
        console.log(`module stub was added to '${dir}'`);
    } else {
        console.log(`module stub was not created, '${dir}' is not empty`)
    }
}
