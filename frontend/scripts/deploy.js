const fs = require('fs');
const path = require('path');
const rimraf = require('rimraf');

process.env.NODE_ENV = 'production';

const buildDir = path.resolve(__dirname, '../build');

function copyDirectorySync(source, destination) {
  if (!fs.existsSync(destination)) {
    fs.mkdirSync(destination);
  }

  const files = fs.readdirSync(source);

  for (const file of files) {
    const sourcePath = path.join(source, file);
    const destinationPath = path.join(destination, file);

    if (fs.lstatSync(sourcePath).isDirectory()) {
      copyDirectorySync(sourcePath, destinationPath);
    } else {
      fs.copyFileSync(sourcePath, destinationPath);
    }
  }
}

const sourceDirectory = path.resolve(buildDir, './static');
const destinationDirectory = path.resolve(`../src/main/resources/static/section`);
rimraf.sync(destinationDirectory);
fs.mkdirSync(destinationDirectory, { recursive: true });
copyDirectorySync(sourceDirectory, destinationDirectory);
console.log(`Copied build outputs.`);
